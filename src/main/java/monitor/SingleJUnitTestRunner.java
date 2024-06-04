package monitor;
import org.junit.runner.JUnitCore;

import org.junit.runner.Request;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class SingleJUnitTestRunner {

    public static void start(){
        System.out.println("Start test");
    }

    public static void end(){
        System.out.println("End test");
    }

    public static void main(String... args) throws ClassNotFoundException {
        try {
            String[] classAndMethod = args[0].split("#");
//            Request request = Request.method(Class.forName(classAndMethod[0]),
//                    classAndMethod[1]);
            Request request;

            if(classAndMethod.length == 1) {
                request = Request.aClass(Class.forName(classAndMethod[0]));
                Result result = new JUnitCore().run(request);
                System.out.println(result.wasSuccessful());
                for (Failure failure:result.getFailures()){
                    System.out.println(failure.getTestHeader());
                }
                return;
            }
            request = Request.method(Class.forName(classAndMethod[0]), classAndMethod[1]);
            Result result = new JUnitCore().run(request);

            System.out.println(result.wasSuccessful());
            for (Failure failure:result.getFailures()) {
                if(failure.getMessage()!=null){
                    String exceptionType = failure.getException().getClass().getName();
                    if(exceptionType.endsWith("TestTimedOutException")){
                        System.out.println("Exception:TestTimedOutException");
                    }else if(exceptionType.endsWith("IndexOutOfBoundsException")){
                        System.out.println("Exception:IndexOutOfBoundsException");
                    }else if(exceptionType.endsWith("ArithmeticException")){
                        System.out.println("Exception:ArithmeticException");
                    }else {
                        System.out.printf("Exception:%s", exceptionType);
                    }
                }
            }
//            System.out.println(result.getFailures());

//        System.exit(result.wasSuccessful() ? 0 : 1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
