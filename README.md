## Developing Maintainable Software COMP 2042 Coursework

# Tetris JavaFX

- GitHub repository link : https://github.com/eashwar910/DMS_Tetris


---
## Compilation Instructions
- Prerequisites: Install JDK `23` and ensure JavaFX media support is available on your system.
- Use the Maven Wrapper in the project root:
    - Compile the code: `./mvnw clean compile`
    - Run the game : `./mvnw javafx:run`
    - Build without running: `./mvnw -DskipTests clean package`
- If you use IntelliJ on Windows to run the game, it might prompt you to download additional dependencies for font loading.


---
## Successfully Implemented Features
1. 7-Bag Randomization System

    - An improvised "randomization" system adopted by popular Tetris games.
    - This ensures that the blocks dont repeat too often or not spawn for a long time which are possible while using proper randomization.
    - Creates a "bag" with the 7 bricks in random order and orientations and adds them to the queue.
    - Refills the "bag" everytime its empty.


2. Overlay Flow (Start, Help, Pause, Game Over)

    - Centralized overlay logic with a countdown before the game starts.
    - Pause toggle with an overlay.
    - Normal Game Over overlay.
    - Game Over with new high-score overlays.


3. Hold Brick and Preview of Next 3 Bricks

    - Hold/swap with `C`.
    - Player can put the current brick into the "Hold" panel and use the next brick and swap it back with any other brick when its falling.
    - Allows the player to alternate between favourable bricks.
    - Panel that shows the next 3 upcoming bricks.
    - Allows the player to use the hold feature during favourable conditions.


4. Hard Drop with Pulse Effect
    - Press `SPACE` to hard drop, landed cells pulse for feedback.
    - Adds a visual depth to the bricks falling on the Game Board floor.
    - Pulse Effect logic :

              if (rect != null)
              {
                  javafx.animation.SequentialTransition pulse = Effects.createPulseEffect(rect);
                  pulse.play();
              }



5. Multiple Game Modes

    5.1 Infinite Mode (Classic)
    - Classic Tetris Game Mode which goes on till the bricks reach a specific height. 
   
    5.2 Timed Mode (2 minutes)
    - Player will be racing against time trying to score as high as possible in 2 minutes. 

    5.3 Bottoms up Mode 
    - Classic Tetris but everything is upside down. 
    - Adds a visual level of confusion to the game making it more engaging. 
    - The brick spawning, falling and everything is upside down. 
    - The roof is now the floor and the player cannot let the bricks drop down far too low. 


6. Dynamic Start Screen
    - An animated start screen fully coded using JavaFX
    - Has a pulsating grid animation in the background with bricks flying across at random speeds and bursting into particles on collision.


7. Music and SFX
    - Start screen music, game play music and game over screen music.
    - Click sounds added as well.


8. High Score per Mode
    - Persisted to `highscore.txt` with separate values for Normal/Timed/Bottoms-Up.
    - This way, you can track your progress in each game mode.


9. Levels 
    - Implemented a linearly increasing level logic 
    - For every 10 lines cleared, the speed of the bricks increase (1 level higher) 
    - It follows a linear equation  
      `double t = Math.pow(Math.max(0.0, base - ((level - 1) * dec)), Math.max(0, level - 1));
ms = Math.max(Constants.MIN_FALL_INTERVAL_MS, t * 1000.0);`


---
## Implemented but Not Working Properly
1. Ghost Brick Preview
    - Translucent landing preview for the bricks so player can calculate the position of their bricks before it falls down.
    - Also works with the upside down mode.
    - Problem : Has a very minute (2 pixel) offset on other devices.
  
2. Audio playback environment
    - JavaFX media relies on platform codecs. On some macOS setups, media may not play without additional components. If audio fails, verify JavaFX Media support or consider WAV-only FX.
    - Click sounds do not play on a few systems. 


--- 
## Deferred Features (Planned but not implemented)

1. Two Player mode 
    - I planed to add a Two Player Mode where two people can play tetris on the same screen using WASD and Arrow keys at the same time.
    - Problem : Could not find proper reference and resources.


---
## New Java Classes

1. `Constants.java` - Holds all the constants used across the projects for easy access.


2. `GameEventListener.java` - Event listener class for all game rendering events.


3. `GameLoopManager.java` - Manages the game loops and flow between different game modes.


4. `KeyboardInputManager.java` - Manages the Keyboard input for playing the game.


5. `Effects.java` - Manages all the javafx animations and effects.


6. `GameRenderer.java` - Renders the game board and sets up the scene for the game.


7. `DynamicStartScreen.java` — Animated start screen with tetromino/particle layers. 


8. `GhostBrickHandler.java` — Ghost landing preview with upside-down support. 


9. `MusicManager.java` — Centralized music/SFX playback. 


10. `OverlayManager.java` — Start/pause/help/game-over overlays and countdowns.


11. `PauseOverlay.java` — Pause overlay UI component. 


12. `GameOverOverlay.java` — Game-over overlay with high-score mode. 


13. `ScorePopup.java` — Score bonus and combo popup component. 


14. `SceneManager.java` — Board centering and layout bindings.


15. `GameModeHandler.java` — Mode management (Normal/Timed/Bottoms-Up) and countdown. 


---
## Modified Java Classes

1. `SimpleBoard.java `$\rightarrow$` GameBoard.java` 

  - Renamed and updated to include logic for holding bricks, managing multiple next-brick previews, and handling collision detection.
  

2. `NotificationPanel.java `$\rightarrow$` ScorePopup.java`
  - Renamed and enhanced to display specific combo labels (Double, Triple, Tetris) alongside score bonuses.


3. `GameOverPanel.java `$\rightarrow$` GameOverOverlay.java` 
  - Renamed and expanded to include "Restart" and "Exit" buttons, as well as a specific display screen for new high scores.


4. `GameController.java`
    - Added hard drop handling with scoring and pulse.
    - Bound new UI effects and mode handling.


5. `GuiController.java`
    - Centralized initialization, renderer/ghost setup, and overlay wiring.
    - Added upside-down visual toggle and pulse effect routing ).
    - Introduced score popups for cleared rows.


6. `MatrixOperations.java`
    - Simplified collision/merge/clear logic; added helpers and score bonus.


7. `Score.java`
    - Per-mode highscores persisted to `highscore.txt`.


8. `Main.java` 
    - Moved to app package and updated to use centralized Constants for window sizing instead of hardcoded numbers.


9. `Board.java`
    - Interface expanded to support holdBrick functionality and clearing hold state.


10. `ViewData.java`
    - Structure updated to store the held brick data and a queue of multiple upcoming bricks for previews.


11. `RandomBrickGenerator.java`
    - Replaced simple random generation with a 7-bag shuffle system to prevent drought/flood of specific pieces.


12. `BrickGenerator.java`
    - Interface updated to allow retrieving a list of upcoming bricks (getNextBricks) for the UI preview.


13. `InputEventListener.java`
    - Added event callbacks for onHoldEvent and onHardDropEvent to support new user controls.


14. `EventType.java`
    - Enum expanded to include HOLD and HARD_DROP event types.


15. `BrickRotator.java`, `MoveEvent.java`, `DownData.java`, `ClearRow.java`, `Brick.java`, `NextShapeInfo.java` & Shape Classes
    - Updated with comprehensive Javadocs and minor formatting cleanup.
    

--- 
## Unexpected Problems

- Centering the game board took me a lot of time because I tried to do it without hardcoding the positions. 


- Resource paths
    - Files loaded via filesystem (e.g., `grid.png`) can fail when running from a JAR. Prefer classpath resources for portability.


- JavaFX Media
    - Audio availability varies across environments. Mitigated by WAV for clicks and bundling MP3s in `src/main/resources/sound/`.

---
