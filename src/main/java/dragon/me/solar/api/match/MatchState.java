package dragon.me.solar.api.match;

/**
 *
 * Represents the state of a match.
 * Be careful when using 'internal' methods as they are not stable due to they being obfuscated.
 */
public enum MatchState {
    STARTING(),
    ONGOING(),
    ENDED();
}
