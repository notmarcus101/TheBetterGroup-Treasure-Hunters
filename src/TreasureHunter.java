import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private boolean hardMode;
    private boolean testMode;
    private boolean easyMode;
    private boolean samuraiMode;
    public static boolean justShopped;

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
        justShopped = false;
    }

    public static void setJustShopped () {
        justShopped = true;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        System.out.println("Welcome to " + Colors.CYAN + "TREASURE HUNTER" + Colors.RESET + "!");
        System.out.println("Going hunting for the big treasure, eh?");
        System.out.print("What's your name, Hunter? ");
        String name = SCANNER.nextLine().toLowerCase();

        // set hunter instance variable
        hunter = new Hunter(name, 10);

        System.out.print("(E)asy, (N)ormal, or (H)ard mode?: ");
        String hard = SCANNER.nextLine().toLowerCase();
        if (hard.equals("h")) {
            hardMode = true;
        } else if (hard.equals("test")) {
            testMode = true;
        } else if (hard.equals("e")) {
            easyMode = true;
        } else if (hard.equals("s")) {
            samuraiMode = true;
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        }
        if (testMode) {
            //in test mode, the hunter starts off with 100 gold
            hunter.changeGold(96);

            //and has every item in their kit
            String[] items = {"water", "rope", "machete", "horse", "boat", "boots"};
            for (String item : items) {
                hunter.buyItem(item, 1);
            }
        }
        if (easyMode) {
            hunter.changeGold(hunter.getGold()*2);
            markdown = 1;
            toughness = 0.2;
            currentTown.easyMode(true);
        }
        if (samuraiMode) {
            Shop.setSamuraiMode();
        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";

        while (!choice.equals("x") && !hunter.bankrupt()) {
            System.out.println();
            if (justShopped) {
                System.out.println("You just left the shop.");
                justShopped = false;
            } else {
                System.out.println(currentTown.getLatestNews());
            }
            System.out.println("***");
            System.out.println(hunter);
            System.out.println(currentTown);
            System.out.println("(H)unt for treasure!");
            System.out.println("(D)ig for gold!");
            System.out.println("(B)uy something at the shop.");
            System.out.println("(S)ell something at the shop.");
            System.out.println("(M)ove on to a different town.");
            System.out.println("(L)ook for trouble!");
            System.out.println("Give up the hunt and e(X)it.");
            System.out.println();
            System.out.print("What's your next move? ");
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
        }
        if (hunter.bankrupt()) {
            System.out.println("You ran out of gold! Try again next time, and maybe don't get into fights you can't win...");
        }
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("h")) {
            System.out.println("You go hunting for treasure in the town!");
            if (currentTown.isSearched()) {
                System.out.println("But, you already searched this town for its treasure. Don't be greedy.");
            } else {
                if (currentTown.huntTreasure()) {
                    if (currentTown.getTownTreasure().equals("crown")) {
                        if (currentTown.getTerrain().getTerrainName().equals("Mountains")) {
                            System.out.println("You found a crown at the peak of the tallest mountain! Who could've left this here..?");
                        } else if (currentTown.getTerrain().getTerrainName().equals("Ocean")) {
                            System.out.println("You notice something shiny on the seabed. You swim towards it and see that it's a (very rusty) crown! Seems like Poseidon lost it here.");
                        } else if (currentTown.getTerrain().getTerrainName().equals("Plains")) {
                            System.out.println("After hours of digging, you finally notice a shimmer of gold under the earth. You dig some more and take your newfound crown out of the hole you've made.");
                        } else if (currentTown.getTerrain().getTerrainName().equals("Desert")) {
                            System.out.println("Sitting atop a cactus, you find a green crown on top of a flowering cactus. You take it, hoping that no one will come to take it back...");
                        } else if (currentTown.getTerrain().getTerrainName().equals("Jungle")) {
                            System.out.println("In a dungeon deep in the jungle, you find a crown on top of a pedestal. Taking it activates a trap, so you hurry out before you get killed.");
                        } else if (currentTown.getTerrain().getTerrainName().equals("Marsh")) {
                            System.out.println("Walking through the muddy marsh, you... wait, what..? I guess the crown was just in your bag the entire time..?");
                        }
                    } else if (currentTown.getTownTreasure().equals("trophy")) {
                         if (currentTown.getTerrain().getTerrainName().equals("Mountains")) {
                            System.out.println("On top of the tallest mountain you've fittingly named \"Everest\", you find a trophy with a glove frozen to its side. You grab it and take the glove off, silently remorseful.");
                        } else if (currentTown.getTerrain().getTerrainName().equals("Ocean")) {
                             System.out.println("In a shipwreck on one of the archipelago's islands, you find a trophy with seaweed and a starfish inside of it. You take it, deciding to preserve its current appearance.");
                         } else if (currentTown.getTerrain().getTerrainName().equals("Plains")) {
                             System.out.println("You won the town's annual \"Town's Toughest Person\" contest, and got the first place trophy! The stacks of green paper is an added benefit, I guess.");
                         } else if (currentTown.getTerrain().getTerrainName().equals("Desert")) {
                             System.out.println("After hours of wandering the vast desert and many mirages disappointing you, you're glad to see a shiny cup of water on the ground. You take it with you, somewhat glad to see it's not a mirage, even more so when you realize what it is.");
                         } else if (currentTown.getTerrain().getTerrainName().equals("Jungle")) {
                             System.out.println("Maybe all of the snake bites and eating poisonous mushrooms was worth it, as you finally find the fabled \"Lost Trophy\" in the middle of some overgrown weeds. Huh, I feel itchy..?");
                         } else if (currentTown.getTerrain().getTerrainName().equals("Marsh")) {
                             System.out.println("You finally got your hands on the Trophy of the Marsh! Now, the problem is how to get out of the quicksand../");
                         }
                    }
                } else {
                    if (currentTown.getTownTreasure().equals("dust")) {
                        System.out.println("You found a pile of dust. You decide it's best to leave it behind...");
                    } else {
                        System.out.println("You already found the " + currentTown.getTownTreasure() + " somewhere!");
                    }
                }
            }
        } else if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                System.out.println(currentTown.getLatestNews());
                enterTown();
            }
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble();
        } else if (choice.equals("x")) {
            System.out.println("Fare thee well, " + hunter.getHunterName() + "!");
        } else if (choice.equals("d")) {
            currentTown.digGold();
        } else {
            System.out.println("Yikes! That's an invalid option! Try again.");
        }
    }
}