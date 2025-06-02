package com.klanting.signclick.economy.logs;

import java.util.UUID;

abstract public class PluginLogs {
    /*
    * A logging system, to log information later accessible in minecraft by users
    * Logs remain for a configurable duration.
    *
    * This class follows the observer design pattern
    * */

    abstract void update(String action, String message, UUID issuer);


}
