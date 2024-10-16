# Minesweeper
This is a minesweeper game made in Scala programming language, using ScalaFX library. It contains multiple levels of difficulty to play, a leaderboard and a level editor. Scores and levels are saved in text files.

![Screenshot 2024-10-16 174527](https://github.com/user-attachments/assets/4f05ec41-fabb-4be0-89f2-168e998af11c)

## New Game
After clicking new game, player chooses level difficulty, then selects a premade level of chosen difficulty or a randomly generated level to play.

![Screenshot 2024-10-16 174544](https://github.com/user-attachments/assets/07b9c9bf-7b61-4c7f-bcb8-71552ef0b49e)
![Screenshot 2024-10-16 174601](https://github.com/user-attachments/assets/fa9d9e5b-a9f9-4e37-85e8-fae500e4c39c)

Player can open any field by using left mouse click or flag it using right mouse click. After opening first field, timer starts. At any time player can generate new random level of chosen difficulty, save current state (and continue later), show a mineless field (Hint button).

![Screenshot 2024-10-16 174645](https://github.com/user-attachments/assets/0173fde9-65fa-4fa0-b6c5-052c6ec8c33b)

## Leaderboard
After finishing a level, a player can enter score their score on the leaderboard. 

![Screenshot 2024-10-16 175436](https://github.com/user-attachments/assets/ca20e208-a088-4658-bde4-fe432a7e35e9)

## Editor
Player can make a custom level, save it and later play it. Mine is put or removed from a field by clicking on it.

<img src="https://github.com/user-attachments/assets/212eb678-1649-4342-b1c4-7390dd1c36a2" alt="" width="450"/>


Selector (S) can be used to select multiple fields. 

<img src="https://github.com/user-attachments/assets/9a022226-0df8-48f3-b3e0-2db1dbb29c8b" alt="" width="450"/>
<img src="https://github.com/user-attachments/assets/ee5b4ede-d885-41d5-b146-d6053b8885a6" alt="" width="450"/>


Pivot (P) is required for transforming selected fields, which can be rotated around it or reflected around chosen axis which contains pivot point.

<img src="https://github.com/user-attachments/assets/d50b7d8a-7a77-4269-9c84-894c61b36844" alt="" width="450"/>
<img src="https://github.com/user-attachments/assets/6e817348-d6be-44d1-9b0d-12b1f3366009" alt="" width="450"/>
