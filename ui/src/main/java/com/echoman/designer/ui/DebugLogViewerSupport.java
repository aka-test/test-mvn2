/**
 * 
 */
package com.echoman.designer.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.TopComponent;

    /**
     * This was copied from org.netbeans.core.actions.LogViewerSupport to use for
     * our own debug log action.
     * @author david.morin
     */
     public class DebugLogViewerSupport implements Runnable {
        boolean shouldStop = false;
        FileInputStream  filestream=null;
        BufferedReader    ins;
        InputOutput io;
        File fileName;
        String ioName;
        int lines;
        Ring ring;
        private final RequestProcessor.Task task = RequestProcessor.getDefault().create(this);

        /** Connects a given process to the output window. Returns immediately, but threads are started that
         * copy streams of the process to/from the output window.
         * @param process process whose streams to connect to the output window
         * @param ioName name of the output window tab to use
         */
        public DebugLogViewerSupport(final File fileName, final String ioName) {

            this.fileName=fileName;
            this.ioName = ioName;
        }

        /**
         * 
         */
        private void init() {
            final int LINES = 2000;
            final int OLD_LINES = 2000;
            ring = new Ring(OLD_LINES);
            String line;

            // Read the log file without
            // displaying everything
            try {
                while ((line = ins.readLine()) != null) {
                    ring.add(line);
                } // end of while ((line = ins.readLine()) != null)
            } catch (IOException e) {
                Logger.getLogger(DebugLogViewerSupport.class.getName()).log(Level.INFO, null, e);
            } // end of try-catch

                                    // Now show the last OLD_LINES
            lines = ring.output();
            ring.setMaxCount(LINES);
        }

        /**
         * 
         */
    @Override
        public void run() {
            final int MAX_LINES = 10000;
            String line;

            ////System.out.println("io close or not"+io.isClosed());
            if (io.isClosed()){//tab is closed by the user
                shouldStop =true;
            }
            else{
                            // it is possilbe in the case of only
                            // 1 tab, that the tab is hidden, not
                            // closed. In this case we need to
                            // detect that and close our stream
                            // anyway to unlock the log file
                shouldStop =true; //assume the tab is hidden
                TopComponent.Registry rr= TopComponent.getRegistry();
                for (TopComponent tc: rr.getOpened()) {
                    if (tc.toString().startsWith("org.netbeans.core.io.ui.IOWindow$IOWindowImpl[Output")){
                        // the tab is not hidden so we should not stopped!!!
                        shouldStop =false;
                        break;
                    }
                }
            }

            if (!shouldStop) {
                try {
                    if (lines >= MAX_LINES) {
                        io.getOut().reset();
                        lines = ring.output();
                    } // end of if (lines >= MAX_LINES)

                    while ((line = ins.readLine()) != null) {
                        if ((line = ring.add(line)) != null) {
                            io.getOut().println(line);
                            lines++;
                        } // end of if ((line = ring.add(line)) != null)
                    }

                }catch (IOException e) {
                    Logger.getLogger(DebugLogViewerSupport.class.getName()).log(Level.INFO, null, e);
                }
                task.schedule(10000);
            }
            else {
                ///System.out.println("end of infinite loop for log viewer\n\n\n\n");
                stopUpdatingLogViewer();
            }
        }

        /** display the log viewer dialog
         *
         */
        public void showLogViewer() throws IOException{
            shouldStop = false;
            io = IOProvider.getDefault().getIO(ioName, false);
            io.getOut().reset();
            io.select();
            filestream = new FileInputStream(fileName);
            ins = new BufferedReader(new InputStreamReader(filestream));

            init();
            task.schedule(0);
        }

        /** stop to update  the log viewer dialog
         *
         */

        public void stopUpdatingLogViewer()   {
            try{
                ins.close();
                filestream.close();
                io.closeInputOutput();
                io.setOutputVisible(false);
            }
            catch (IOException e){
                Logger.getLogger(DebugLogViewerSupport.class.getName()).log(Level.INFO, null, e);
            }
        }

        /**
         *
         */
        private class Ring {
            private int maxCount;
            private int count;
            private LinkedList<String> anchor;

            /**
             * 
             * @param max
             */
            public Ring(int max) {
                maxCount = max;
                count = 0;
                anchor = new LinkedList<String>();
            }

            /**
             * 
             * @param line
             * @return
             */
            public String add(String line) {
                if (line == null || line.equals("")) { // NOI18N
                    return null;
                } // end of if (line == null || line.equals(""))

                while (count >= maxCount) {
                    anchor.removeFirst();
                    count--;
                } // end of while (count >= maxCount)

                anchor.addLast(line);
                count++;

                return line;
            }

            /**
             * 
             * @param newMax
             */
            public void setMaxCount(int newMax) {
                maxCount = newMax;
            }

            /**
             * 
             * @return
             */
            public int output() {
                int i = 0;
                for (String s: anchor) {
                    io.getOut().println(s);
                    i++;
                }

                return i;
            }

            /**
             * 
             */
            public void reset() {
                anchor = new LinkedList<String>();
            }
        }
    }
