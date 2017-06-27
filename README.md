# WhatToWatch
Created during the first and second projects in Udacity Android Developer Nanodegree.
The purpose of this app, is to help users discover popular and highly rated movies using the themoviedb.org web API. 

<img src="https://www.themoviedb.org/assets/static_cache/bb45549239e25f1770d5f76727bcd7c0/images/v4/logos/408x161-powered-by-rectangle-blue.png" width="102" height="40" alt="Powered by TMDB">

## Features
* Sort by most popular movies, top rated movies or by previously favored movies.
* Tap on a movie poster and transition to a details screen with additional information.

## Screenshots
[![Most Popular Movies](https://s2.postimg.org/v0yl8js2h/Screenshot_1498533768_framed.png)](https://postimg.org/image/ffh9olg45/)
[![Top Rated Movies](https://s22.postimg.org/ejgdhd09t/938797e2-82ee-47aa-92ab-f2cda16b9ec4.png)](https://postimg.org/image/spw4clb4t/)
[![My Favorite Movies](https://s4.postimg.org/4vpgl7di5/df13f8b0-487e-4c17-9857-3e66e7afe228.png)](https://postimg.org/image/szg89hvyx/)

## Development

### API Key
The app uses themoviedb.org API to get movie information. You must provide your own API key in order to build the app.
Just put your API key into ~/.gradle/gradle.properties file (create the file if it doesn't already exists).

Add the following line, replacing the value with your key.

``` TMDB_API_KEY="YOUR_API_KEY" ```
