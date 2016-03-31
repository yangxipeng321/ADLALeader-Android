package hk.com.mobileye.jason.adlaleader.common.logger;

import android.util.Log;
/**
 * Created by Jason on 2014/12/29.
 * Helper class which wraps Android's native Log utility in the Logger interface. This way
 * normal DDMS output can be one of the many targets receiving and outputting logs simultaneously.
 */
public class LogWrapper implements LogNode{

    //For piping: The next node to receive Log data after this one has done its work.
    private LogNode mNext;

    /**
     * Returns the next LogNode in the linked list.
     */
    public LogNode getNext(){return mNext;}

    /**
     * Sets the LogNode data will be sent to..
     */
    public void setNext(LogNode node){mNext=node;}


    /**
     * Prints data out to the console using Android's native log mechanism.
     *
     * @param priority  Log level of the data being logged. Verbose, Error, etc.
     * @param tag       Tag for the log data. Can be used to organize log statements.
     * @param msg       The actual message to be logged.
     * @param tr        If an exception was thrown, this can be sent along for the logging facilities
     */
    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {
        //There actually are log methods that don't take a msg parameter. For now,
        //if that's the case, just convert null to the empty and move on.
        String useMsg = msg;
        if (useMsg == null) {
            useMsg = "";
        }

        //If an exception was provided, convert that exception to a usable string and attach
        //it to the end of the msg method.
        if (tr != null) {
            useMsg += "\n" + Log.getStackTraceString(tr);
        }

        //This is functionally identical to Log.x(tag, useMsg);
        //For instance, if priority were Log.VERBOSE, this would be the same as Log.v(tag, useMsg)
        Log.println(priority, tag, useMsg);

        if (mNext != null) {
            mNext.println(priority, tag, msg, tr);
        }

    }
}
