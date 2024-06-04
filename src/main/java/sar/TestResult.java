package sar;
import java.io.*;

public class TestResult {
    private boolean result;
    private boolean isExcept;
    private JavaException exception = null;


    public boolean getResult() {
        return result;
    }
    public void setResult(boolean result) {
        this.result = result;
    }
    public JavaException getException() {
        return exception;
    }

    public boolean isExcept() {
        return isExcept;
    }
    public  TestResult(Boolean result){
        this.result = result;
    }
    public TestResult(String logPath) throws IOException {
        this.result = false;
        this.isExcept = false;
        if(new File(logPath).exists()) {
            FileInputStream fStream = new FileInputStream(logPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fStream));
            String line = br.readLine();
            while (line != null) {
                if (line.startsWith("true"))
                    result = true;
                if (line.startsWith("false"))
                    result = false;
                if(line.startsWith("Exception:")) {
                    line = line.replace("\n", "");
                    this.isExcept = true;
                    this.exception = new JavaException(line.split(":")[1]);
                }
                line = br.readLine();
            }
        }
    }

    public void setExceptionLocation(int location){
        exception.setLocation(location);
    }


    public static class JavaException{
        String exceptionType;
        int location;

        JavaException(String exceptionType){
            this.exceptionType = exceptionType;
        }

        public void setLocation(int location) {
            this.location = location;
        }

        @Override
        public String toString() {
            return String.format("Exception:%s Occurred at line %d.", this.exceptionType, this.location);
        }

        public int getLocation() {
            return location;
        }
    }

}
