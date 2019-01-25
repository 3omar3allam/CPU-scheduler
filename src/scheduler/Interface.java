package scheduler;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;

import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.TOP_CENTER;

public class Interface extends Application {
    private Stage window;
    private VBox scene_box;
    private ArrayList<Node> grid_content;
    private TableView<Process> table;
    private TextField addName;
    private TextField addArrival;
    private TextField addBurst;
    private TextField addPriority;
    private Button start;
    private TextField quantum_field;

    private ObservableList<Process> process_list = FXCollections.observableArrayList();
    private static int nameCounter = 1;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("CPU Scheduler");
        GridPane grid = new GridPane();
        grid_content = new ArrayList<>(3);
        scene_box = new VBox(10);
        grid.setPadding(new Insets(10));

        set_algotithms(0, 0);
        StackPane dummy = new StackPane();
        dummy.setMinSize(50,200);
        GridPane.setConstraints(dummy,1,0);
        grid_content.add(dummy);
        init_table(2,0);
        grid.getChildren().addAll(grid_content);

        scene_box.getChildren().addAll(grid);
        set_outputFrame();

        //Display:
        Scene scene = new Scene(scene_box, 750, 600);
        window.setScene(scene);
        window.setOnCloseRequest(e -> {
            e.consume();
            close();
        });
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    private void set_algotithms(int column, int row){
        // algorithms:
        VBox algorithm_box = new VBox(10);
        ArrayList<Node> nodes = new ArrayList<>();

        Label algorithm_label = new Label("Scheduling Algorithm:");
        nodes.add(algorithm_label);
        ToggleGroup AlgorithmToggle = new ToggleGroup();

        RadioButton FCFS = new RadioButton("First Come First Served");
        FCFS.setUserData("FCFS");
        nodes.add(FCFS);

        RadioButton SJF = new RadioButton("Shortest Job First");
        SJF.setUserData("SJF");
        nodes.add(SJF);

        RadioButton SRTF = new RadioButton("Shortest Remaining Time First");
        SRTF.setUserData("SRTF");
        nodes.add(SRTF);

        RadioButton priority = new RadioButton("Priority-Based Scheduling (Not Preemptive)");
        priority.setUserData("priority");
        nodes.add(priority);

        RadioButton priority_preemptive = new RadioButton("Priority-Based Scheduling (Preemptive)");
        priority_preemptive.setUserData("priority_preemptive");
        nodes.add(priority_preemptive);

        RadioButton RR = new RadioButton("Round Robin");
        HBox RR_box = new HBox(15);
        quantum_field = new TextField();
        quantum_field.setPromptText("Quantum");
        quantum_field.setMaxWidth(80);
        quantum_field.setDisable(true);
        RR_box.getChildren().addAll(RR,quantum_field);
        RR.setUserData("RR");
        nodes.add(RR_box);

        AlgorithmToggle.getToggles().addAll(FCFS,SJF,SRTF, priority,priority_preemptive,RR);
        AlgorithmToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            String newAlgo = newValue.getUserData().toString();
            String oldAlgo = "";
            if(oldValue == null);
            else oldAlgo = oldValue.getUserData().toString();
            if(newAlgo.equals("RR")) {
                quantum_field.setDisable(false);
                make_mandatory(quantum_field,true);
            }
            else if(oldAlgo.equals("RR")){
                quantum_field.setDisable(true);
                make_mandatory(quantum_field,false);
            }
            if( (! oldAlgo.equals("priority") && ! oldAlgo.equals("priority_preemptive"))
                 && (newAlgo.equals("priority") || newAlgo.equals("priority_preemptive")) ) make_mandatory(addPriority,true);
            if( (oldAlgo.equals("priority") || oldAlgo.equals("priority_preemptive"))
                 && (!newAlgo.equals("priority") && !newAlgo.equals("priority_preemptive")) )make_mandatory(addPriority,false);
            start.setUserData(newAlgo);
        });

        Label label = new Label("NOTE: Mandatory fields are bordered.\n(You Don't have to enter unused info)");
        label.setPadding(new Insets(15));
        label.setMinHeight(120);
        label.setAlignment(Pos.BOTTOM_CENTER);
        nodes.add(label);
        nodes.add(set_buttons());

        algorithm_box.getChildren().addAll(nodes);
        GridPane.setConstraints(algorithm_box,column,row);
        grid_content.add(algorithm_box);
    }
    private void init_table(int column,int row){
        VBox entry_contents = new VBox(10);
        Label label = new Label("Enter The Processes:");
        table = new TableView<>();

        table.setEditable(true);

        table.setMinHeight(250);
        TableColumn nameColumn = new TableColumn("Process Name");
        nameColumn.setMinWidth(100);
        nameColumn.setStyle("-fx-alignment: CENTER");
        TableColumn arrivalColumn = new TableColumn("Arrival");
        arrivalColumn.setMinWidth(100);
        arrivalColumn.setStyle("-fx-alignment: CENTER");
        TableColumn burstColumn = new TableColumn("CPU Burst");
        burstColumn.setMinWidth(100);
        burstColumn.setStyle("-fx-alignment: CENTER");
        TableColumn priorityColumn = new TableColumn("Priority");
        priorityColumn.setMinWidth(100);
        priorityColumn.setStyle("-fx-alignment: CENTER");

        nameColumn.setCellValueFactory(new PropertyValueFactory<Process,String>("name"));
        arrivalColumn.setCellValueFactory(new PropertyValueFactory<Process,Double>("arrival"));
        burstColumn.setCellValueFactory(new PropertyValueFactory<Process,Integer>("burst"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<Process,Integer>("priority"));
        table.setItems(process_list);

        HBox entry_fields = new HBox();
        addName = new TextField();
        addName.setMaxWidth(nameColumn.getMinWidth());
        addName.setAlignment(CENTER);
        addName.setPromptText("Process Name");
        addArrival = new TextField();
        addArrival.setMaxWidth(arrivalColumn.getMinWidth());
        addArrival.setAlignment(CENTER);
        addArrival.setPromptText("Arrival");
        addBurst = new TextField();
        addBurst.setMaxWidth(burstColumn.getMinWidth());
        addBurst.setAlignment(CENTER);
        addBurst.setPromptText("Burst");
        make_mandatory(addBurst,true);
        addPriority = new TextField();
        addPriority.setMaxWidth(priorityColumn.getMinWidth());
        addPriority.setAlignment(CENTER);
        addPriority.setPromptText("Priority");

        entry_fields.getChildren().addAll(addName,addArrival,addBurst,addPriority);

        table.getColumns().addAll(nameColumn, arrivalColumn, burstColumn,priorityColumn);

        HBox process_buttons = new HBox(10);
        Button add = new Button("+");
        Button remove = new Button("-");
        Button clear = new Button("Clear All");
        process_buttons.getChildren().addAll(add, remove, clear);

        add.setOnAction(e -> {
            String algorithm = start.getUserData().toString();
            if(addName.getText().equals("")) addName.setText("P"+Integer.toString(nameCounter));
            nameCounter++;
            if(addArrival.getText().equals("")) addArrival.setText("0");

            if(algorithm.equals("priority") || algorithm.equals("priority_preemptive")){
                try {
                    process_list.add(new Process(addName.getText(), Double.parseDouble(addArrival.getText()), Integer.parseInt(addBurst.getText()), Integer.parseInt(addPriority.getText())));
                    addName.setText("P"+Integer.toString(nameCounter));
                } catch (Exception ex) {
                    error("Invalid Input Format!");
                }
            }
            else{
                if(addPriority.getText().equals("")) {
                    try {
                        process_list.add(new Process(addName.getText(), Double.parseDouble(addArrival.getText()), Integer.parseInt(addBurst.getText()),0));
                        addName.setText("P"+Integer.toString(nameCounter));
                    } catch (Exception ex) {
                        error("Invalid Input Format!");
                    }
                }
                else{
                    try {
                        process_list.add(new Process(addName.getText(), Double.parseDouble(addArrival.getText()), Integer.parseInt(addBurst.getText()), Integer.parseInt(addPriority.getText())));
                        addName.setText("P"+Integer.toString(nameCounter));
                    } catch (Exception ex) {
                        error("Invalid Input Format!");
                    }
                }
            }

        });
        remove.setOnAction(e -> {
            if(process_list.isEmpty());
            else {
                addName.setText(process_list.get(process_list.size()-1).name);
                addArrival.setText( Double.toString(process_list.get(process_list.size()-1).arrival) );
                addBurst.setText( Integer.toString(process_list.get(process_list.size()-1).burst) );
                addPriority.setText( Integer.toString(process_list.get(process_list.size()-1).priority) );
                process_list.remove(process_list.size()-1);
                nameCounter--;
            }
        });
        clear.setOnAction(e -> {
            table.getItems().clear();
            addArrival.clear();
            addName.clear();
            addBurst.clear();
            addPriority.clear();
            nameCounter = 1;
        });


        entry_contents.setPadding(new Insets(10));
        process_buttons.setAlignment(Pos.CENTER_RIGHT);
        entry_contents.getChildren().addAll(label,table,entry_fields,process_buttons);
        GridPane.setConstraints(entry_contents,column,row);
        grid_content.add(entry_contents);
    }
    private Node set_buttons() {
        HBox buttons = new HBox(25);
        buttons.setPadding(new Insets(10));
        buttons.setMinHeight(70);
        start = new Button("Start");
        start.setUserData("");
        start.setMinWidth(100);
        start.setOnAction(e -> {
            if (process_list.isEmpty()) error("Schedule is Empty!");
            else if (start.getUserData().toString().equals("")) error("Choose an Algorithm!");
            else {
                String algorithm = start.getUserData().toString();
                if (!algorithm.equals("RR")) displayChart(new Scheduler(algorithm, process_list));
                else {
                    if (quantum_field.getText().equals("")) error("Enter the Chunk for Round Robin");
                    else {
                        try {
                            int quantum = Integer.parseInt(quantum_field.getText());
                            displayChart(new Scheduler(algorithm, process_list, quantum));
                        } catch (NumberFormatException ex) {
                            error("Invalid Input format!");
                        }
                    }
                }
            }
        });
        buttons.getChildren().addAll(start);
        buttons.setAlignment(Pos.BOTTOM_LEFT);
        return buttons;
    }
    private void set_outputFrame(){
        VBox frame = new VBox(2);
        frame.setMinHeight(140);
        frame.setMinWidth(500);
        frame.setMaxWidth(500);
        frame.setAlignment(CENTER);
        scene_box.getChildren().addAll(new Separator(),frame);
    }
    private void close(){
        boolean close = Close.display("Close","Are you sure you want to close?");
        if(close) window.close();
    }
    private void error(String message){
        StackPane error_frame = new StackPane();
        Label error = new Label(message);
        error.setAlignment(CENTER);
        error.setStyle("-fx-font-size: 20");
        error.setTextFill(Color.RED);
        error.setMinHeight(140);
        error_frame.setAlignment(CENTER);
        error_frame.getChildren().add(error);
        scene_box.getChildren().set(2,error_frame);
    }
    private void displayChart(Scheduler scheduler){
        VBox Frame = new VBox(5);
        Frame.setMinHeight(140);
        Frame.setMinWidth(600);
        Frame.setAlignment(CENTER);

        HBox hbox = new HBox(0);
        hbox.setMinHeight(60);
        hbox.setMaxHeight(60);
        hbox.setAlignment(CENTER);
        double unit_width = (double)500 / scheduler.getTotalTime();
        int timeline = 0;
        for( ChartSegment segment : scheduler.chart){
            VBox item = new VBox(1);
            Label label = new Label(Integer.toString(timeline));
            label.setAlignment(Pos.BOTTOM_LEFT);
            label.setMinHeight(14);
            label.setMaxHeight(14);

            Label colored = new Label(segment.name);
            colored.setAlignment(CENTER);
            colored.setTextFill(Color.BLACK);
            colored.setStyle("-fx-background-color: "+segment.color + "; -fx-font-size: 16");
            colored.setMinWidth(unit_width * segment.length);
            colored.setMinHeight(35);
            colored.setMaxHeight(35);

            item.getChildren().addAll(colored,label);
            hbox.getChildren().add(item);

            timeline += segment.length;
        }
        Label end = new Label(Integer.toString(timeline));
        end.setMinHeight(50);
        end.setMaxHeight(50);
        end.setAlignment(Pos.BOTTOM_CENTER);
        hbox.getChildren().add(end);

        Label time = new Label();
        Process.average_waiting_time = Double.parseDouble(String.format("%.2f",Process.average_waiting_time));
        Process.average_turnaround_time = Double.parseDouble(String.format("%.2f",Process.average_turnaround_time));
        time.setText("Average Turnaround Time = "+Process.average_turnaround_time
            + "\t\t\tAverage Waiting Time = "+Process.average_waiting_time);
        time.setStyle("-fx-font-size: 16");

        Frame.getChildren().addAll(hbox,time);
        scene_box.getChildren().set(2,Frame);
    }
    private void make_mandatory(TextField field, boolean positive){
        String text = field.getPromptText();
        if(positive){
            text += " *";
            field.setStyle("-fx-border-color:darksalmon");
        }
        else {
            text = text.substring(0,text.length()-2);
            field.setStyle("-fx-border-style:none");
        }
        field.setPromptText(text);
    }
}