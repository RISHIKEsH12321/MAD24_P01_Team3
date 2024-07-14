package sg.edu.np.mad.travelhub;

import java.util.List;

public class ChatbotItinerary {
    private String type;
    private List<DayPlan> plan;

    // Getters and setters

    public static class DayPlan {
        private int day;
        private List<Activity> activities;

        // Getters and setters

        public static class Activity {
            private String time;
            private String description;

            // Getters and setters
        }
    }
}