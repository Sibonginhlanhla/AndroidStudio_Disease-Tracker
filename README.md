# Disease Tracker

This Android app is designed to assist with contact tracing during the Covid-19 pandemic. The goal is to help users voluntarily track their presence at different locations and be notified if they are at risk for Covid-19 based on reports from other users. The app promotes public safety by allowing users to check in at locations, report diagnoses, and query their risk of exposure to the virus.

## About The Project

The Disease Tracker application allows users to:
1. Check in at predefined locations, indicating they are at a specific place on a particular day.
2. Report a Covid-19 diagnosis, which helps identify potential exposure risks for others.
3. Check for risk of exposure by querying whether they were at the same location on the same day as someone diagnosed with Covid-19.
4. View a list of locations and see the number of infected users who checked in at those places.

### Key Features

- Predefined Locations: Users can select locations from a predefined list (no GPS support required, but optional for future enhancements).
- Covid-19 Diagnosis Reporting: Users can update their status if they are diagnosed, which is used for contact tracing.
- Exposure Check: Users can see if they were at risk based on shared location data.
- Infection Count by Location: Users can view how many people diagnosed with Covid-19 have been to each location.

## Getting Started

To contribute to the Disease Tracker project, please follow the guidelines below.

### Prerequisites

- Android Studio: Ensure you have the latest version of Android Studio installed.
- Java or Kotlin Knowledge: The app is built in Android Studio, so knowledge of Java or Kotlin is essential.

### Installation

1. Clone the repository: git clone https://github.com/Sibonginhlanhla/AndroidStudio_Disease-Tracker.git

2. Open the project in Android Studio:
- Launch Android Studio and open the cloned repository.
- Install any necessary SDKs or dependencies as prompted by Android Studio.

3. Backend Setup:
- Set up the PHP backend on a server.
- Update the API base URL in the Android app code to point to your backend server.

4. Database Setup:
- Create a new MySQL database.
- Import the `database.sql` file located in the backend directory to set up the required tables.

5. Run the App:
- Build and run the app on an Android emulator or a physical device.

### Usage Instructions

- **Check In at a Location:** Users can navigate to the "Check In" screen, select a location from the predefined list, and choose the date they visited.
- **Report a Covid-19 Diagnosis:** From the settings or profile screen, users can indicate that they have been diagnosed with Covid-19. This will update the database and notify other users at risk.
- **Check Risk of Exposure:** Users can query whether they were at a specific location on the same day as someone diagnosed with Covid-19.
- **View Location Statistics:** The app provides a list of locations along with the number of infected individuals who have checked in.

### Important Notes

- **No direct push to the main branch:** Always create your own branch and submit a pull request to merge.
- **Sprint Contributions:** Ensure that there is a push after every sprint or milestone.

### Future Enhancements

- **GPS Integration:** Support for GPS-based location check-ins.
- **Notifications:** Implement push notifications to alert users if they have been exposed to someone with Covid-19.
- **Backend Support:** Integrate a backend API for handling large-scale data and providing real-time updates.

