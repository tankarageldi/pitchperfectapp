package Controller;

import Model.Flashcard;
import View.*;

/**
 * The DrillViewer class is responsible for managing the visual components of a
 * drill session.
 * It initializes and controls the display of musical elements such as clefs,
 * hands, notes, and a timer.
 * The class interacts with the UI to create and update these components based
 * on the provided flashcards.
 * It also includes a private inner class, DrillTimer, to handle countdown
 * timing for the drill session.
 */
public class DrillViewer {
    private UI ui;
    private Controller controller;
    private NoteMapping noteCoords;
    private ImageComponent clef;
    private ImageComponent leftHand;
    private ImageComponent rightHand;
    private ImageComponent[] notes;
    private TextComponent timer;
    private DrillTimer drillTimer; // Private timer class for this DrillViewer
    private ButtonComponent backButton;
    private int[] menuSize;

    /**
     * Initializes the DrillViewer with the given UI and controller.
     *
     * @param ui The UI instance to be used by the DrillViewer.
     * @param controller The Controller instance to be used by the DrillViewer.
     */
    public DrillViewer(UI ui, Controller controller) {
        noteCoords = new NoteMapping();
        this.ui = ui;
        this.controller = controller;
        menuSize = new int[]{0, ui.getScreenWidth(), 0, ui.getScreenHeight()};
    }

    /**
     * Initializes the drill by setting up the flashcard components.
     */
    public void initializeDrill() {
        // Setting up the flashcard
        int[] clefCoords = { 170, 0, 0, 0 };
        int clefID = ui.createViewComponent("image");
        ui.getViewComponent(clefID).updateXY(clefCoords);
        clef = (ImageComponent) ui.getViewComponent(clefID);
        clef.setHidden(true);

        int[] leftHandCoords = { 155, 0, 600, 0 };
        int leftHandID = ui.createViewComponent("image");
        ui.getViewComponent(leftHandID).updateXY(leftHandCoords);
        leftHand = (ImageComponent) ui.getViewComponent(leftHandID);
        leftHand.setHidden(true);

        int[] rightHandCoords = { 995, 0, 600, 0 };
        int rightHandID = ui.createViewComponent("image");
        ui.getViewComponent(rightHandID).updateXY(rightHandCoords);
        rightHand = (ImageComponent) ui.getViewComponent(rightHandID);
        rightHand.setHidden(true);

        int[] timerCoords = { 400, 0, 100, 0 };
        int timerID = ui.createViewComponent("text");
        ui.getViewComponent(timerID).updateXY(timerCoords);
        timer = (TextComponent) ui.getViewComponent(timerID);
        timer.setHidden(true);
        
        int[] backButtonCoords = {0, 100, menuSize[2], 100};
        int backButtonID = ui.createViewComponent("button");
        backButton = (ButtonComponent) ui.getViewComponent(backButtonID);
        backButton.updateXY(backButtonCoords);
        backButton.setHidden(true);
        backButton.setMessage("back");
        backButton.setText("Main Menu");
        controller.addParsable(backButtonID);

        notes = new ImageComponent[4];
        for (int i = 0; i < notes.length; i++) {
            notes[i] = createImageComponent(new int[] { 0, 0, 0, 0 });
        }
    }

    /**
     * Creates an ImageComponent with the specified coordinates.
     *
     * @param coords The coordinates for the ImageComponent.
     * @return The created ImageComponent.
     */
    private ImageComponent createImageComponent(int[] coords) {
        int id = ui.createViewComponent("image");
        ui.getViewComponent(id).updateXY(coords);
        ImageComponent component = (ImageComponent) ui.getViewComponent(id);
        component.setHidden(true);
        return component;
    }

    /**
     * Creates a TextComponent with the specified coordinates.
     *
     * @param coords The coordinates for the TextComponent.
     * @return The created TextComponent.
     */
    private TextComponent createTextComponent(int[] coords) {
        int id = ui.createViewComponent("text");
        ui.getViewComponent(id).updateXY(coords);
        TextComponent component = (TextComponent) ui.getViewComponent(id);
        component.setHidden(true);
        return component;
    }

    /**
     * Loads the given flashcard into the DrillViewer.
     *
     * @param flashcard The flashcard to be loaded.
     */
    public void loadFlashcard(Flashcard flashcard) {
        int[] clefCoords = { 170, 1190, 0, 800 };
        clef.setXY(clefCoords);

        int[] leftHandCoords = { 155, 355, 600, 800 };
        leftHand.setXY(leftHandCoords);

        int[] rightHandCoords = { 995, 1195, 600, 800 };
        rightHand.setXY(rightHandCoords);

        for (int i = 0; i < notes.length; i++) {
            notes[i].setHidden(true);
        }

        for (int i = 0; i < flashcard.getAnswer().length; i++) {
            int[] noteCoordsArray = noteCoords.getCoordinates(flashcard.getAnswer()[i], flashcard.getClef());
            notes[i].setXY(noteCoordsArray);

            String path = noteCoords.getImagePath(flashcard.getAnswer()[i], flashcard.getClef());
            notes[i].changeImage(path);

            notes[i].setHidden(false);
        }
        if (flashcard.getClef() == 'T') {
            clef.changeImage("/Assets/trebleStaff.png");
        } else if (flashcard.getClef() == 'B') {
            clef.changeImage("/Assets/bassStaff.png");
        }
        clef.setHidden(false);

        if (flashcard.getHand() == 'L') {
            leftHand.changeImage("/Assets/leftHandFilled.png");
            rightHand.changeImage("/Assets/rightHandBlank.png");
        } else if (flashcard.getHand() == 'R') {
            leftHand.changeImage("/Assets/leftHandBlank.png");
            rightHand.changeImage("/Assets/rightHandFilled.png");
        }
        leftHand.setHidden(false);
        rightHand.setHidden(false);
        backButton.setHidden(false);
    }

    /**
     * Closes the DrillViewer by hiding all components.
     */
    public void close() {
        clef.setHidden(true);
        leftHand.setHidden(true);
        rightHand.setHidden(true);
        backButton.setHidden(true);

        for (ImageComponent note : notes) {
            note.setHidden(true);
        }
    }

    /**
     * Starts the timer with the given start time.
     *
     * @param startTime The initial time for the timer in seconds.
     */
    public void startTimer(int startTime) {
        if (drillTimer != null) {
            drillTimer.stop();
        }

        drillTimer = new DrillTimer(startTime);
        drillTimer.start();
    }

    /**
     * Stops the timer.
     */
    public void stopTimer() {
        if (drillTimer != null) {
            drillTimer.stop();
        }
        closeTimer();
    }

    /**
     * Updates the timer display with the specified time and makes it visible.
     *
     * @param time The time to be displayed on the timer.
     */
    private void loadTimer(int time) {
        timer.setText(String.valueOf(time));
        timer.setHidden(false);
    }

    /**
     * Hides the timer by setting its visibility to hidden.
     */
    private void closeTimer() {
        timer.setHidden(true);
    }

    /**
     * DrillTimer is a private inner class that extends Thread to create a countdown
     * timer.
     * The timer starts from a specified time and decrements every second until it
     * reaches zero or is stopped.
     */
    private class DrillTimer extends Thread {
        private int time;
        private volatile boolean running = true;

        /**
         * Constructs a DrillTimer with the specified start time.
         *
         * @param startTime The initial time for the timer in seconds.
         */
        public DrillTimer(int startTime) {
            this.time = startTime;
        }

        @Override
        public void run() {
            while (time > 0 && running) {
                loadTimer(time); // Update UI with remaining time
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
                time--;
            }

            if (running) {
                loadTimer(0); // Show "0" when timer ends
            }
        }

        /**
         * Stops the timer.
         */
        public void stopTimer() {
            running = false;
            this.interrupt();
        }
    }
}
