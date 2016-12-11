package edu.nyu.cs.cs2580;

/**
 * Created by sanchitmehta on 10/12/16.
 */

import java.io.*;


public class QueryLogger {
    private String _logRootPath;
    private String _query;
    private SearchEngine.Options _options;


    public QueryLogger(String query){
        this._query = query.toLowerCase();
        try {
            _options = new SearchEngine.Options("conf/engine.conf");
        }catch(Exception e){
            //pass
        }
        _logRootPath = _options._logPrefix + "/";
    }

    public int compareQueries(String current) {
        return current.compareTo(_query);
    }

    public int[] queryExistsInLogs() throws IOException {
        int countOfLines = -1, startIndex = 1, results[] = {-1, -1};

        boolean exists = false;

        File file = new File(_logRootPath + _query.charAt(0) + ".tsv");
        if (!file.exists()) {
            return results;
        }

        String line;
        FileReader f = new FileReader(_logRootPath + _query.charAt(0) + ".tsv");
        BufferedReader br = new BufferedReader(f);
        while ((line = br.readLine()) != null) {
            countOfLines++;
            String lineArr[] = line.split("\t");
            //First line is count
            if (countOfLines != 0) {
                int val = compareQueries(lineArr[0]);
                if (val == 0) {
                    exists = true;
                    break;
                } else if (val < 0) {
                    startIndex = countOfLines;
                } else {
                    startIndex = countOfLines;
                    break;
                }
            }
        }
        br.close();
        if (exists) {
            results[0] = 1;
            results[1] = countOfLines;
        } else {
            results[0] = 0;
            results[1] = startIndex;
        }
        return results;
    }

    public void writeToFile() {
        try {
            int results[] = queryExistsInLogs();
            int lineCount = 0;
            String line;
            StringBuilder builder = new StringBuilder(_logRootPath).append(_query.charAt(0) + "temp.tsv");
            BufferedWriter writer = new BufferedWriter(new FileWriter(builder.toString(), true));
            if (results[0] == -1 && results[1] == -1) {
                writer.write(1 + "\n");
                writer.write(_query + "\t" + 1 + "\n");
            } else {
                FileReader fr = new FileReader(_logRootPath + _query.charAt(0) + ".tsv");
                BufferedReader br = new BufferedReader(fr);

                while ((line = br.readLine()) != null) {
                    if (results[0] == 0) {
                        if (lineCount == 0) {
                            Integer x = Integer.parseInt(line) + 1;
                            line = x.toString();
                        } else if (lineCount == results[1]) {
                            writer.write(_query + "\t" + 1 + "\n");
                            writer.write(line + "\n");
                            break;
                        }
                    } else {
                        if (lineCount == results[1]) {
                            String lineArr[] = line.split("\t");
                            Integer x = Integer.parseInt(lineArr[1]) + 1;
                            line = lineArr[0] + "\t" + x.toString();
                            writer.write(line + "\n");
                            break;
                        }
                    }
                    writer.write(line + "\n");
                    lineCount++;
                }

                while ((line = br.readLine()) != null) {
                    writer.write(line + "\n");
                }
                br.close();
            }
            writer.close();
            renameFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renameFiles() {
        File oldFile = new File(_logRootPath + _query.charAt(0) + "temp.tsv");
        File newFile = new File(_logRootPath + _query.charAt(0) + ".tsv");
        if (newFile.exists()) {
            newFile.delete();
        }
        oldFile.renameTo(newFile);
        oldFile.delete();
    }
}