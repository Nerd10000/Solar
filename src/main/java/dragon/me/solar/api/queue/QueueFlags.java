package dragon.me.solar.api.queue;

/**
 * Represents the attributes of a queue.
 */
public interface QueueFlags {

    /**
     * Gets the weight of the queue.
     * @return None or ELO depending on the queue.
     */
    String getWeight();

    /**
     *
     * @return how many players does 1 team have.
     */
    int getTeamSize();

    /**
     *
     * @return 'true' if parties are allowed to join the queue.
     */
    boolean arePartiesAllowed();

    /**
     * Get the minimum rating you'll need to join the queue.
     * @return minimum rating that the player will need to join the queue.
     */
    int getMinRating();

    /**
     * Gets the permission requirement of the queue.
     * @return the permission that is required to join the queue.
     */
    String getPermission();

    /**
     * Gets the minimum size of the party
     * @return the minimum size of a party that can join the queue.
     */
    int getMinPartySize();

    /**
     * Gets the maximum party size that a party can have and still join the party.
     * @return maximum amount of players that are allowed to join with a party.
     */
    int getMaxPartySize();
}
