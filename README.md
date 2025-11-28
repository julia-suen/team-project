# Team Project - WildFire

## Group name - wildfireiq

### Project Domain
| Wildfire Tracker
The goal of this project is to create an interactive map of wildfire data across Canada to identify and flag susceptible / highly afflicted areas to develop more effective ways to mitigate risks in the future. Since data updates nearly instantly, this programme stays relevant with time. 

### User Stories
1. As a user, I want to analyse wildfire trends in my province over a range of time
2. As a user, I want to hover over a wildfire marker to see the specifics of it (location - latitude, longitude; size; date)
3. As a user, I am trying to observe trends in size and scale of wildfires in Canada over the past 3 months
4. As a user, I would like to drag around the map to focus on specific areas, and zoom-in to get a close-up view of any one region
5. As a user, I want to filter fires by severity (moderate / high) to focus on areas that are especially susceptible to large-scale fires (determined by Fire Radiation Power)
6. As a user, I want to favourite a location that I can revisit to check for updates at a later time

### API Information
- The Fire Information for Resource Management System (`FIRMS`) w/ Near Real-Time (NRT) active fire data NOAA 20 for the US and Canada active fire detections are available in real-time
  - https://firms.modaps.eosdis.nasa.gov/api/
- `nomatim` for boundaries

### Working Demo
- screenshots or animations demonstrating current functionality

### Advisory Notes
- This project uses NASA API data, which was subject to temporary outage due to the government shutdown in the US. Since the shutdown has been lifted, only 3 months worth of data remain in the API, which is what we are currently working with; although in theory, it should work even with a much larger dataset
- The API updates at a near real-time rate, so there may be inconsistencies in two realisations of the same query

