/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private String townTreasure;
    private static String[] treasure = {"crown", "trophy", "gem", "dust"};
    private String town = "";
    private boolean townTreasureFound = false;
    public boolean easyMode = false;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        townTreasure = treasure[(int) (Math.random() * 4)];

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }

    public void easyMode (boolean condition) { //town's version
        easyMode = condition;
    }

    public String getLatestNews() {
        return printMessage;
    }

    public String getTownTreasure() {
        return townTreasure;
    }

    public void updateNews (String str) {
        printMessage = str;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                if (!(item.equals("horse") || item.equals("rope") || item.equals("water"))) {
                    printMessage += "\nUnfortunately, your " + Colors.PURPLE + item + Colors.RESET + " broke.";
                    getLatestNews();
                } else {
                    printMessage += "\nUnfortunately, you lost your " + Colors.PURPLE + item + Colors.RESET + " item.";
                    getLatestNews();
                }
            }

            return true;
        } else {
            printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + Colors.PURPLE + terrain.getNeededItem() + Colors.RESET + ".";
            getLatestNews();
            return false;
        }
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
            if (toughTown) {
                noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }

        if (Math.random() > noTroubleChance) {
            printMessage = Colors.RED + "You couldn't find any trouble" + Colors.RESET;
        } else {
            if (hunter.hasItemInKit("sword")) {
                int goldDiff = (int) (Math.random() * 10) + 1;
                printMessage = Colors.RED + "You want trouble strange.... h-hey now.. drop that sword!.. I ain't mean no harm!\nHave mercy! I have a family! Take what you want!" + Colors.RESET;
                hunter.changeGold(goldDiff);
                printMessage += Colors.RED + "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RED + " gold." + Colors.RESET;
            } else {
                printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
                int goldDiff = (int) (Math.random() * 10) + 1;
                if (Math.random() > noTroubleChance) {
                    printMessage += Colors.RED + "Okay, stranger! You proved yer mettle. Here, take my gold.";
                    printMessage += Colors.RED + "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RED + " gold." + Colors.RESET;
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += Colors.RED + "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                    printMessage += Colors.RED + "\nYou lost the brawl and pay " + Colors.YELLOW + goldDiff + Colors.RED + " gold." + Colors.RESET;
                    hunter.changeGold(-goldDiff);
                }
            }
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        int rnd = (int) (Math.random() * 12) + 1;
        if (rnd < 2) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < 4) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < 6) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < 8) {
            return new Terrain("Desert", "Water");
        } else if (rnd < 10) {
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh ", "Boots");
        }
    }

    public Terrain getTerrain() {
        return terrain;
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        if (easyMode) {
            return false;
        }
        double rand = Math.random();
        return (rand < 0.5);
    }

    public void digGold () {
        if (hunter.hasItemInKit("shovel")) {
            int chance = (int) (Math.random() * 10) + 1;
            int goldChance = (int) (Math.random() * 20) + 1;
            if (!terrain.getTerrainName().equals(town)) {
                if (chance <= 5) {
                    hunter.changeGold(goldChance);
                    System.out.println("You dug up " + goldChance + " gold!");
                    town = terrain.getTerrainName();
                } else {
                    System.out.println("You dug up nothing but dirt, boo hoo.");
                }
            } else {
                System.out.println("You already dug for gold in this town!");
            }
        } else {
            System.out.println("You don't have a shovel to dig with!");
        }
    }

    public boolean isSearched() {
        return townTreasureFound;
    }

    public boolean huntTreasure() {
        if (!townTreasure.equals("dust")) {
            if (hunter.addTreasureToList(townTreasure)) {
                townTreasureFound = true;
                return true;
            }
            return false;
        }
        return false;
    }
}