# (MAD24_P01_3) PlanHub: Your Ultimate Travel Companion

## Team Members:
- Lim Weiqin Ian
- Vallimuthu Rishikesh
- Brandon Koh Ziheng
- Vincent Hernando
- An Yong Shyan

## Introduction to the App
Category: **Travel & Local** <br> 
Introducing PlanHub, your travel companion, designed to simplify every aspect of your journey. From seamless login and customizations to intuitive event management and currency conversion, our basic features ensure effortless planning and exploration along with a customizable touch to your app. As you progress to more advanced features, unlock collaborative planning with event invitations, community engagement through interactive travel posts, and personalized recommendations powered by intelligent algorithms. With biometric authentication and timely notifications, PlanHub elevates your travel experience, making every adventure memorable and hassle-free. Join us as we redefine the way you travel.


## Features

| Name        | Feature           | Stage  |
|:------------- |:-------------|:----- |
| Vincent | Login / Signup | Stage 1 |
| Brandon | Settings Page | Stage 1 |
| Brandon | Edit Profile | Stage 1 |
| Brandon | View Account | Stage 1 |
| Ian | Get Recommended Places (Hardcoded) | Stage 1 |
| Rishikesh | Manage Trips | Stage 1 |
| Yong Shyan | Colour Themes | Stage 1 |
| Yong Shyan | Currency Converter | Stage 1 |
| Brandon | Search Users | Stage 2 |
| Brandon | Swipe RecyclerView to Follow & to Delete| Stage 2 |
| Brandon | Follow Users | Stage 2 |
| Brandon | Message Users in Real-Time | Stage 2 |
| Yong Shyan | Travel Chatbot | Stage 2 |
| Yong Shyan | Post Collaboration Between Users | Stage 2 |
| Ian | AutoComplete Predictions Search | Stage 2 |
| Ian | Places API Implementation | Stage 2 |
| Ian | Place Recommendation Engine | Stage 2 |
| Ian | Pagination Recyclerview | Stage 2 |
| Ian | Bottom Sheet Dragger | Stage 2 |
| Ian | Get Current User Phone Location | Stage 2 |
| Vincent | Forum Post About Recommended Planned Trips | Stage 2 |
| Rishikesh | Biometric Authentication | Stage 2 |
| Rishikesh | Notifications | Stage 2 |
| Rishikesh | Share Events by Generating and Scanning a QR code | Stage 2 |
| Rishikesh | Drag to Change Order of Elements when Creating/Editing Events | Stage 2 |
## Stage 1 - App Features

### Login / Sign up (Vincent)
- Profile Picture
- Bio
- Email
- Password
- Logout

### Settings Page (Brandon)
- Change and Remember Permissions
- Enable Notifications Switch button (implemented in stage 2)
- Fingerprint Switch button (Enabled / Disabled) (implemented in stage 2)

### Edit Profile (Brandon)
- Change Id (username)
- Change Bio
- Change Email

### View Account (Brandon)
- View own profile picture
- View own name
- View own description
- See all of user's own posts (implemented in stage 2)

### Get Recommended Places (Global/Local) [Currently Hard-Coded] (Ian)
- **Filter buttons**
  - Sightseeing Places
  - Landmarks

### Manage Trip (Rishikesh)
- **Create, Edit & Delete Event Details**
  - Title
  - Description (To-do list)
  - Date / Time
  - Adding events to calendars

### Color Themes (An Yong Shyan)
- **Color blind themes**
  - Protanopia
  - Deuteranopia
  - Tritanopia
- Light mode
- Watermelon theme
- Neon theme

### Currency Converter (An Yong Shyan)
- Use **API** to update the currency to current value before calculating

## Stage 2 - App Features

### Search and View Users (Brandon)
- Users can search for another user and click to see their favourite places, posts, and follower/following user count
- Users can see their followed users at the top for easy access

### Follow Users (Brandon)
- Users can follow another user to access their profile easier from the profile page to see their current posts and favourite places
- Users can see another user's followed and following users by clicking on the follower/following user count on their profile page

### Swipe RecyclerView to Follow / Delete (Brandon)
- Users can swipe left on a user's RecyclerView item to follow them (in SearchUser) or to delete items (in EventManagement) without having to click on their profile
- The change will immediately be reflected in the search users page, where the user will rise to the top of the list after getting followed
- When Managing an Event, users can swipe items to the left to delete it. Also includes undo function.

### Message Users in Real-Time (Brandon)
- Users can message another user and exchange information in real-time, with messages immediately updating for both users

### Chatbot for Users (An Yong Shyan)
- Chatbot for users to interact with, specifically about travel, with predefined questions

### Auto-complete / Minor Spelling Error Search (Ian)
- Auto-suggest places
- Suggest places despite minor spelling errors

### Places API Implementation (Ian)
- Search feature
- Get places dynamically
- Round Robin Distribution for even display for place categories

### Place Recommendation Engine (Ian)
- Implemented a scoring based system on the different categories/kinds of places from the user's favorite places as well as their viewing histories of different plasces
- Places categories/kinds with the highest score would be recommended to the user's under the "All" fitler

### Pagination Recyclerview (Ian)
- Implemented a scroll to the end of the recyclerview for more places, aka pagination

### Bottom Sheet Dragger (Ian)
- Implemented a bottom sheet dragger in one of my activities

### Get Current User Phone Location (Ian)
- With location permissions granted on the user's phone, the user's location will be retrieved and they would be able to find recommended places from their current locaiton

### Forum Post - Create Post About Recommended Planned Trips (Vincent)
- Create/Edit/Delete posts
- Posts may or may not contain events
- Other users can view posts
- View user profiles and their posts
- User comments (if time allows)

### Implement Biometric Authentication for Logging In (Rishikesh)
- Biometric Authentication enabled during logging in

### Notification / Reminder (Rishikesh)
- Notify the user based on the time and date they set

### QR Code Generator And Scanner (Rishikesh)
- Generates a QR code and displays it.
- Allows other users to scan it and make their own local copy of the event

### Drag to Change Order of Elements when Creating/Editing Events(Rishikesh)
- Allows user to drag and drop items such as reminders, so that they can view it in the order they want.

### Event Collaboration with Multiple Users (Yong Shyan)
- Allows multiple users to edit the same post
---
## Appendix
>Prototyping + Wireframe: [Figma](https://www.figma.com/design/fJuuFJDSka7JnnyRCV05IA/MAD-Assignment?node-id=0-1&t=Jmfqz8S3lv9UrODC-1) <br>
>Logo Design: [Canva](https://www.canva.com/design/DAGEnn9sDLw/bp4kC-6HD0xlGx8cYC2ARA/edit?utm_content=DAGEnn9sDLw&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)
