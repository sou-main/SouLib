package soulib.logLib;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class GameLogFormatter extends Formatter{
	private final SimpleDateFormat sdFormat2
    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

public String format(final LogRecord argLogRecord) {
    final StringBuffer buf = new StringBuffer();
    buf.append(argLogRecord.getLoggerName());
    buf.append(" - ");
	buf.append(sdFormat2.format(new Date(argLogRecord.getMillis())));
	buf.append(" ");
    if (argLogRecord.getLevel() == Level.FINEST) {
        buf.append("FINEST");
    } else if (argLogRecord.getLevel() == Level.FINER) {
        buf.append("FINER ");
    } else if (argLogRecord.getLevel() == Level.FINE) {
        buf.append("FINE  ");
    } else if (argLogRecord.getLevel() == Level.CONFIG) {
        buf.append("CONFIG");
    } else if (argLogRecord.getLevel() == Level.INFO) {
        buf.append("INFO  ");
    } else if (argLogRecord.getLevel() == Level.WARNING) {
        buf.append("WARN  ");
    } else if (argLogRecord.getLevel() == Level.SEVERE) {
        buf.append("SEVERE");
    } else {
        buf.append(Integer.toString(argLogRecord.getLevel()
            .intValue()));
        buf.append(" ");
    }
    buf.append("- ");
    buf.append(argLogRecord.getMessage());
    buf.append(System.getProperty("line.separator"));

    return buf.toString();
}

}
