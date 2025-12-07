# Omada take home

Application using the Flickr API to display recent photos as well as search for photos. You can also view details about a photo when tapping on them.

I used clean architecture principles with Compose, MVVM, Retrofit, Coroutines, Hilt. For testing I used Mockk, Truth, and Turbine.

Per instructions, the app has a search text box at the top that only searches when the user presses enter/search. If there is no text in the box, it will show recent photos. The images are laid out in a grid with a 3 column layout and square images. Error handling is done by showing an error message instead of the grid layout. There is a simple CircularProgressIndicator when loading. Otherwise, for the Success state, the images are displayed. Pagination was implemented manually without using the google paging library. Bonus points included a detail screen when tapping on a photo, and I used navigation 3 to navigate between the screens. View models were unit tested.

To run the app, you need to add your own Flickr API key in the `local.properties` file, as such: `flickr_api="YOUR_API_KEY"`

video of app working: [![video](./docs/Screen_recording_20251207_183059.mp4)](./docs/Screen_recording_20251207_183059.mp4)