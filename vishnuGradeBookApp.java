import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;

public class vishnuGradeBookApp extends Application {

    private Label firstNameLabel = new Label("First Name:");
    private Label lastNameLabel = new Label("Last Name:");
    private Label courseLabel = new Label("Course:");
    private Label gradeLabel = new Label("Grade:");

    private TableView<Student> tableView = new TableView<>();
    private ObservableList<Student> students = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {

        // Creating textfields
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField courseField = new TextField();
        ComboBox<String> gradeComboBox = new ComboBox<>();
        gradeComboBox.getItems().addAll("A", "B", "C", "D","E", "F");

        // Adding Buttons
        Button clearButton = new Button("Clear");
        Button saveButton = new Button("Save");
        Button viewButton = new Button("View Saved Grades");

      
        // Layout for the form, buttons, and table
        GridPane formGridPane = new GridPane();
        formGridPane.setPadding(new Insets(20));
        formGridPane.setHgap(10);
        formGridPane.setVgap(10);
        formGridPane.setAlignment(Pos.CENTER);

        // Adding form fields to the grid
        formGridPane.add(firstNameLabel, 0, 0);
        formGridPane.add(firstNameField, 1, 0);
        formGridPane.add(lastNameLabel, 0, 1);
        formGridPane.add(lastNameField, 1, 1);
        formGridPane.add(courseLabel, 0, 2);
        formGridPane.add(courseField, 1, 2);
        formGridPane.add(gradeLabel, 0, 3);
        formGridPane.add(gradeComboBox, 1, 3);

        // Adding buttons to the grid
        HBox buttonRow = new HBox(10);
        buttonRow.getChildren().addAll(clearButton, saveButton);
        buttonRow.setAlignment(Pos.CENTER);
        formGridPane.add(buttonRow, 1, 4);

        // Adding "View Grades" button
        HBox viewButtonRow = new HBox(10);
        viewButtonRow.getChildren().addAll(viewButton);
        viewButtonRow.setAlignment(Pos.CENTER);
        formGridPane.add(viewButtonRow, 1, 5);

        // Centering the elements in the grid
        for (int i = 0; i < 4; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(javafx.scene.layout.Priority.ALWAYS);
            formGridPane.getColumnConstraints().add(columnConstraints);
        }

        // Adding ScrollPane 
        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(600);

        // VBox to hold the form and table
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(formGridPane, scrollPane);
        vbox.setAlignment(Pos.CENTER);

        // Apply light blue background color to the application
        vbox.setStyle("-fx-background-color: lightblue;");

        // Scene and stage setup
        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setTitle("Grade Book Form");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Clear button functionality
        clearButton.setOnAction(event -> {
            firstNameField.clear();
            lastNameField.clear();
            courseField.clear();
            gradeComboBox.getSelectionModel().clearSelection();
        });

        // Save button functionality
        saveButton.setOnAction(event -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String course = courseField.getText();
            String grade = gradeComboBox.getValue();

            if (firstName.isEmpty() || lastName.isEmpty() || course.isEmpty() || grade == null) {
                showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill all the fields");
                return;
            }

            Student student = new Student(firstName, lastName, course, grade);

            // Write to CSV file with header
            File file = new File("grades.csv");
            boolean fileExists = file.exists();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                if (!fileExists) {
                    writer.write("FirstName,LastName,Course,Grade");
                    writer.newLine();
                }
                writer.write(student.toString());
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            showAlert(Alert.AlertType.INFORMATION, "Success!", "Grade saved successfully.");
        });

        // View button functionality
        viewButton.setOnAction(event -> {
            students.clear();
            tableView.getColumns().clear(); // Clearing existing columns before adding new ones
            
            // Adding Table columns
            TableColumn<Student, String> firstNameColumn = new TableColumn<>("First Name");
            firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        
            TableColumn<Student, String> lastNameColumn = new TableColumn<>("Last Name");
            lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        
            TableColumn<Student, String> courseColumn = new TableColumn<>("Course");
            courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        
            TableColumn<Student, String> gradeColumn = new TableColumn<>("Grade");
            gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        
            tableView.getColumns().addAll(firstNameColumn, lastNameColumn, courseColumn, gradeColumn);
            
            // Setting width for the table columns
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
            try (BufferedReader reader = new BufferedReader(new FileReader("grades.csv"))) {
                String line = reader.readLine(); 
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 4) {
                        students.add(new Student(data[0], data[1], data[2], data[3]));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            tableView.setItems(students);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}