package com.adasleader.jason.adasleader.common.logger;

/**
 * Created by Jason on 2014/12/29.
 * Simple {@link LogNode} filter, removes everything except the message.
 * Userful for situations like on-screen log output where you don't wat a lot of metadata displayed,
 * just easy-to-read message updates as they're happening.
 */
public class MessageOnlyLogFilter implements LogNode{
    LogNode mNext;

    /**
     * Takes the "next" LogNode as a paramenter, to simplify chaining.
     *
     * @param next The next LogNode in the pipeline.
     */
    public MessageOnlyLogFilter(LogNode next) { mNext= next; }
    public MessageOnlyLogFilter(){}

    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {
        if (mNext != null) {
            getNext().println(Log.NONE, null, msg, null);
        }
    }

    /**
     * Returns the next LogNode in the chain.
     */
    public LogNode getNext() {
        return mNext;
    }

    /**
     * Sets the LogNode data will be sent to..
     */
    public  void setNext(LogNode node){mNext= node;}
}
