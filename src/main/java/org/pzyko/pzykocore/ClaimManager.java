package org.pzyko.pzykocore;

import org.bukkit.World;

import java.util.Optional;

public class ClaimManager {

    // skal fungere som buffer mellom sql og serveren
    // lagre instanser av claims lokalt for å minimere stress på sql, heller bruke mer minne
    // Synkronisert liste for å tåle async loading
    // loade claims som er innenfor en viss radius av alle spillere til enhver tid
    // unloade om alle spillere drar fra området

    public Optional<Object> getTopClaim(World world, double x, double y) {
        // return a buffered instance if exists

        // else select from database and create a new instance

        // return the new instance if exists

        // return empty if no claim found
        return Optional.empty();

        // every N seconds update the buffered instances from database,
        // or remove them if not used in x amount of time
    }

}
