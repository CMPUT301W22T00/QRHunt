# QRHunt Specs

## Owner Specifications: 
 - Images are not stored online 
 - Moderation
   - Ability to delate QR codes that are bad or malicious 
   - Able to delete players

## Player Profile Specifications:
- Username 
- Contact information (Not sure what what implies, email maybe) -- we can add a feature where the player can toggle different contact info to be visible or not 
- View + Add/Remove QR codes (need to update the total QR codes scanned + sum total score + ranking) that the player owns (when the player presses the QR card, it shows the information about the QR code such as the photo, comment, geolocation, value, ranking(?))
 - Additional: ability to sort the QR codes from highest value to lowest value/lowest value to highest value/by rank (?) -- can use this everywhere*
- Player's current score (sum of QR code values)
- Player's total number of scanned QR codes 
- Player's estimated ranking for highest scoring unique QR code 
- Player's estimated ranking for total number of QR codes scanned 

## Log-In Specification:
- For the primary device, they don't need to log in every time (we can do a "remember me" for the primary device); if they add another device, they'll need to log in and approve the additional device 

## QR Code Specifications:
- Scanned by phone camera (approve access to phone camera, camera "button"); the code is scanned but the info it holds isn't recorded (e.g., scan your vaccination ID but it doesn't record the vaccination information on the ID, just the QR code itself) 
- Add geolocation (however, the player can decline sharing the geo location) 
- Add photo of the location + comments associated with the photo 
- Can see that other players have scanned the same QR code (I think we can do this by showing the number of people who scanned it, not their actual profiles) 
- Players have the ability to generate QR codes which show the player's game status (save feature to save the QR code to post later, maybe) or to log in to another device with the same account (see Log-In Specification)

## Search Specifications:
- Search for other players profiles by their usernames (can view their profiles and see the QR codes that they had scanned)--we can add a feature where the player viewing another player's QR code can see how far a certain QR code is from their present location 
- Search for nearby QR codes by using geolocation 
 - Also, see a map of the nearby geolocations (we can do a "sliding" feature similar to Snapchat possibly) 

## Ranking Board Specifications:
- See game-wide high scores of all players (either by sum of QR codes or by total amount of QR codes scanned)
- If  the player viewing the ranking board can see themselves, they should be personally "highlighted" so they know where they are in the ranking board

## Notes:
- When the player or owner first opens the app they will either see: log-in screen / registration UI or "home" UI 
