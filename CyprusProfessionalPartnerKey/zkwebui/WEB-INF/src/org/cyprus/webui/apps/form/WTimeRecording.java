package org.cyprus.webui.apps.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.cyprus.webui.component.Window;
import org.cyprusbrs.apps.TimeRecordDTO;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Timebox;

public class WTimeRecording extends Window implements EventListener {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 private List<TimeRecordDTO> records;
	
	private Grid timeRecordingGrid;
    private Button okButton;
    private Button cancelButton;

	public WTimeRecording() {
        setBorder("normal");
        setWidth("500px");
        setClosable(true);
        setSizable(true);
        Caption caption = new Caption();
        caption.setLabel("Time Recording");
        caption.setStyle("font-weight: bold;");
        caption.setParent(this);
        
        initComponents();
    }
    
    private void initComponents() {
    	timeRecordingGrid = new Grid();
        timeRecordingGrid.setParent(this);
        
        Columns columns = new Columns();
        columns.setParent(timeRecordingGrid);
        
        Rows rows = new Rows();
        rows.setParent(timeRecordingGrid);
        
        Row headerRow = new Row();
        headerRow.setParent(rows);
        headerRow.setStyle("font-weight:bold; font-size:12px;");
        
        Label activityLabel = new Label("Activity");
        activityLabel.setParent(headerRow);
        activityLabel.setStyle("font-weight:bold; font-size:12px;");
        
        Label uomLabel = new Label("UOM");
        uomLabel.setParent(headerRow);
        uomLabel.setStyle("font-weight:bold; font-size:12px;");
        
        Label timeLabel = new Label("Time");
        timeLabel.setParent(headerRow);
        timeLabel.setWidth("120px");
        timeLabel.setStyle("font-weight:bold; font-size:12px;");
        
        List<Map<String, Object>> activities = fetchActivities();
        for (Map<String, Object> activity : activities) {
            addActivityRow(rows, 
        		(Integer)activity.get("C_Activity_ID"),
        		(Integer)activity.get("C_UOM_ID"), 
        		(String)activity.get("Activity"),                
        		(String)activity.get("UOM"),               
        		null
            );
        }
        
        Hbox buttonBox = new Hbox();
        buttonBox.setParent(this);
        buttonBox.setStyle("margin-top:20px; justify-content:center");
        
        okButton = new Button();
        okButton.setParent(buttonBox);
        okButton.setStyle("margin-left:380px; width:50px; height:30px;");
        okButton.setImage("images/Ok24.png");
        okButton.addEventListener(Events.ON_CLICK, this);
        
        cancelButton = new Button();
        cancelButton.setParent(buttonBox);
        cancelButton.setImage("images/Cancel24.png");
        cancelButton.setStyle("width:50px; height:30px;");
        cancelButton.addEventListener(Events.ON_CLICK, this);
    }
    
    private void addActivityRow(Rows rows, int activityId, int uomId, String activity, String uom, Timestamp time) {
    	Row row = new Row();
        row.setParent(rows);
        row.setAttribute("C_Activity_ID", activityId);
        row.setAttribute("C_UOM_ID", uomId);
        new Label(activity).setParent(row);
        new Label(uom).setParent(row);
        Timebox timeBox = new Timebox(time);
        timeBox.setParent(row);
        timeBox.setStyle("width:90%;");
    }
    
    @Override
    public void onEvent(Event event) throws Exception {
    	if (event.getTarget() == okButton){
            	List<TimeRecordDTO> records = processTimeEntries();
            	 records.forEach(record -> {
                     System.out.println("Saved: " + record.getActivityId() 
                         + " - " + record.getTime() + " " + record.getUOMID());
                 });
            	 Events.postEvent(new Event("onTimeSave", this, records));
                 this.detach();
            } 
    	else if (event.getTarget() == cancelButton) {
                this.detach();
            }
    }
    
    private List<TimeRecordDTO> processTimeEntries() {
        List<TimeRecordDTO> records = new ArrayList<>();
        
        List<Component> children = (List<Component>) timeRecordingGrid.getRows().getChildren();
        
        for (Component rowComp : children) {
            if (!(rowComp instanceof Row)) continue;
            
            Row row = (Row) rowComp;
            if (row.getAttribute("C_Activity_ID") == null) continue;
            
            records.add(createRecordFromRow(row));
        }
        return records;
    }

    private TimeRecordDTO createRecordFromRow(Row row) {
    	Integer activityId = (Integer) row.getAttribute("C_Activity_ID");
        Integer uomId = (Integer) row.getAttribute("C_UOM_ID");
            String activity = ((Label) row.getChildren().get(0)).getValue();
            String uom = ((Label) row.getChildren().get(1)).getValue();
            Timebox timebox = (Timebox) row.getChildren().get(2); 
            Date timeValue = timebox.getValue();
            Timestamp time = timeValue != null ? new Timestamp(timeValue.getTime()) : null;
            
            return new TimeRecordDTO(
                    activityId, activity, uomId, uom, time);
    }
//        return new TimeRecordDTO(
//            (Integer) row.getAttribute("C_Activity_ID"),
//            ((Label) row.getChildren().get(0)).getValue(),
//            (Integer) row.getAttribute("C_UOM_ID"),
//            ((Label) row.getChildren().get(1)).getValue(),
//            ((Timestamp)(Timebox) row.getChildren().get(2)).getValue();
//        );
//    }
    
    private List<Map<String, Object>> fetchActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT act.Name AS Activity, act.C_Activity_ID, uom.Name AS UOM, act.C_UOM_ID FROM C_Activity act JOIN C_UOM uom ON (uom.C_UOM_ID=act.C_UOM_ID) ORDER BY Line";
            pstmt = DB.prepareStatement(sql,null);
            rs = pstmt.executeQuery();          
            while (rs.next()) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("Activity", rs.getString("Activity"));
                activity.put("C_Activity_ID", rs.getInt("C_Activity_ID"));
                activity.put("UOM", rs.getString("UOM"));
                activity.put("C_UOM_ID", rs.getInt("C_UOM_ID"));
                activities.add(activity);
            }
        } catch (SQLException e) {
            CLogger.get().log(Level.SEVERE, "Error fetching activities", e);
        } finally {
            DB.close(rs, pstmt);
            rs = null; pstmt = null;
        }
        return activities;
    }
    
    public List<TimeRecordDTO> getTimeRecords() {
        return Collections.unmodifiableList(records); // Prevent external modification
    }
    
}