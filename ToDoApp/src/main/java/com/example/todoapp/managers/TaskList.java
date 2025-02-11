package com.example.todoapp.managers;

import com.example.todoapp.dto.TaskDTO;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TaskList  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String FILE_PATH = "src/task.bin";

    private static TaskList instance;  // Singleton instance
    private List<TaskDTO> tasks;

    private TaskList() {               // Private constructor
        tasks = new ArrayList<>();
        loadTasks();
    }

    // Public method to get the singleton instance
    public static TaskList getInstance() {
        if (instance == null) {
            instance = new TaskList();
        }
        return instance;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    public void addTask(TaskDTO task) {
        tasks.add(task);
        sortTaskByStatus();
        saveTasks();
    }

    public void removeTask(TaskDTO task) {
        tasks.remove(task);
        saveTasks();
    }

    public TaskDTO getTaskById(String id) {
        for (TaskDTO currentTask : tasks) {
            if (currentTask.getId().equals(id)) {
                return currentTask;
            }
        }
        return null;
    }

    public void updateTask(TaskDTO updatedTask) {
        for (int i = 0; i < tasks.size(); i++) {
            TaskDTO currentTask = tasks.get(i);
            if (currentTask.getId().equals(updatedTask.getId())) {
                currentTask.setTitle(updatedTask.getTitle());
                currentTask.setDescription(updatedTask.getDescription());
                currentTask.setStatus(updatedTask.getStatus());
                currentTask.setComments(updatedTask.getComments());
                break;
            }
        }
        sortTaskByStatus();
        saveTasks();
    }

    private void sortTaskByStatus() {
        tasks.sort(Comparator.comparingInt(
                task -> {
                    switch (task.getStatus()){
                        case "ToDo": return 1;
                        case "InProgress": return 2;
                        case "Done": return 3;
                        default: return 4;
                    }
                }
        ));
    }

    private void saveTasks(){
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FILE_PATH))){
            oos.writeObject(tasks);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    private void loadTasks(){
        File file = new File(FILE_PATH);
        if(file.exists()){
            try(ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                tasks = (List<TaskDTO>) ois.readObject();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            saveTasks();
        }
    }


}