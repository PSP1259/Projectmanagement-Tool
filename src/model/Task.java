package model;

/**
 * Represents a single task in the project management tool.
 * <p>
 * This class is a pure data object (Model) and contains no GUI logic.
 * It strictly applies the principle of encapsulation.
 */
public class Task {

    /** The title of this task. */
    private String title;

    /** The description of this task. */
    private String description;

    /** The current status of this task. */
    private String status;

    /** The creation date of this task. */
    private String creationDate;

    /** The deadline of this task. */
    private String deadline;

    /** The total time spent on this task in seconds. */
    private int timeSpentInSeconds;

    /** The assigned users for this task. */
    private String assignees;

    /** The comments associated with this task. */
    private String comments;

    /**
     * Creates a basic task with title, description, and status.
     * <p>
     * Time spent is initialized to 0, and other fields are initialized to empty strings.
     *
     * @param title  the title of the task
     * @param description  the detailed description of the task
     * @param status  the initial status of the task
     */
    public Task(String title, String description, String status) {
        this(title, description, status, 0, "", "", "", "");
    }

    /**
     * Creates a task with all available attributes.
     *
     * @param title  the title of the task
     * @param description  the detailed description of the task
     * @param status  the status of the task
     * @param timeSpentInSeconds  the time spent on the task in seconds
     * @param creationDate  the creation date of the task
     * @param deadline  the deadline of the task
     * @param assignees  the assignees of the task, null treated as empty string
     * @param comments  the initial comments, null treated as empty string
     */
    public Task(String title, String description, String status, int timeSpentInSeconds, String creationDate, String deadline, String assignees, String comments) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.timeSpentInSeconds = timeSpentInSeconds;
        this.creationDate = creationDate;
        this.deadline = deadline;
        this.assignees = assignees != null ? assignees : "";
        this.comments = comments != null ? comments : "";
    }

    /**
     * Gets the title of this task.
     *
     * @return the title
     */
    public String getTitle(){
        return title;
    }

    /**
     * Sets the title of this task.
     *
     * @param title  the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of this task.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this task.
     *
     * @param description  the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the status of this task.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of this task.
     *
     * @param status  the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the time spent on this task in seconds.
     *
     * @return the time spent in seconds
     */
    public int getTimeSpentInSeconds () {
        return timeSpentInSeconds;
    }

    /**
     * Adds the specified amount of time to the total time spent on this task.
     *
     * @param seconds  the time to add in seconds
     */
    public void addTimeInSeconds(int seconds) {
        this.timeSpentInSeconds += seconds;
    }

    /**
     * Sets the time spent on this task in seconds.
     *
     * @param seconds  the new time spent in seconds
     */
    public void setTimeSpentInSeconds(int seconds) {
        this.timeSpentInSeconds = seconds;
    }

    /**
     * Gets the creation date of this task.
     *
     * @return the creation date
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Gets the deadline of this task.
     *
     * @return the deadline
     */
    public String getDeadline() {
        return deadline;
    }

    /**
     * Sets the deadline of this task.
     *
     * @param deadline  the new deadline
     */
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    /**
     * Gets the assignees of this task.
     *
     * @return the assignees
     */
    public String getAssignees(){
        return assignees;
    }

    /**
     * Sets the assignees of this task.
     *
     * @param assignees  the new assignees, may be null
     */
    public void setAssignees(String assignees) {
        this.assignees = assignees;
    }

    /**
     * Gets the comments of this task.
     *
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * Adds a newly formatted HTML comment to this task.
     *
     * @param author  the author of the comment
     * @param text  the content of the comment
     * @param date  the date the comment was made
     */
    public void addComment(String author, String text, String date) {
        this.comments += "💬 <b>" + author + "</b> <i>(" + date + ")</i>: " + text + "<br>";
    }

    /**
     * Returns a string representation of this task.
     *
     * @return a string representation including the title and status
     */
    @Override
    public String toString() {
        return title + " [" + status + "]";
    }
}
