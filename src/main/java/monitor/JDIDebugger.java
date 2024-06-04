package monitor;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.StepRequest;


public class JDIDebugger {
    public final static String JUNIT_TEST_RUNNER = "monitor.SingleJUnitTestRunner";
    public final static String JDI_DEBUGGER = "monitor.JDIDebugger";
    public final static String ROOT_PATH = System.getProperty("user.dir");
    public final static String JUNIT_PATH = Paths.get(ROOT_PATH,"..","..","..","testlib","junit-4.13.2.jar").toString();
    public final static String HAMCREST_PATH = Paths.get(ROOT_PATH, "..","..","..","testlib","hamcrest-2.2.jar").toString();

    //the class and method to be tested
    private final String testeeClass;
    private final String testeeMethod;
    private final String testeeClassPath;

    //the classpath and class and method to test, used in method connectAndLaunchVM()
    private final String testerClass;
    private final String testerClassPath;
    private final String testerMethod;

    private int[] breakPointLines;
    private List<BreakpointRequest> breakpointRequests;
    private final ArrayList<String> logger;


    public JDIDebugger(String testeeClassPath,String testeeClass, String testeeMethod,
                       String testerClassPath,String testerClass){
        this.logger = new ArrayList<>();
        this.testeeClass = testeeClass;
        this.testeeMethod = testeeMethod;
        this.testeeClassPath = testeeClassPath;

        this.testerClassPath = testerClassPath;
        this.testerClass = testerClass;
        this.testerMethod = "";
    }

    public JDIDebugger(String testeeClassPath,String testeeClass, String testeeMethod,
                       String testerClassPath,String testerClass, String testerMethod){
        this.logger = new ArrayList<>();
        this.testeeClass = testeeClass;
        this.testeeMethod = testeeMethod;
        this.testeeClassPath = testeeClassPath;

        this.testerClassPath = testerClassPath;
        this.testerClass = testerClass;
        this.testerMethod = testerMethod;
    }

    public ArrayList<String> getLogger() {
        return logger;
    }

    public String getTesterClass() {
        return testerClass;
    }

    public String getTesterClassPath() {
        return testerClassPath;
    }

    public String getTesterMethod() {
        return testerMethod;
    }

    public String getTesteeClass() {
        return testeeClass;
    }

    public String getTesteeMethod() {
        return testeeMethod;
    }


    public int[] getBreakPointLines() {
        return breakPointLines;
    }

    public List<BreakpointRequest> getBreakpointRequests() {
        return breakpointRequests;
    }

    public void setBreakpointRequests(List<BreakpointRequest> breakpointRequests) {
        this.breakpointRequests = breakpointRequests;
    }

    public void setBreakPointLines(int[] breakPointLines) {
        this.breakPointLines = breakPointLines;
    }

    /**
     * Sets the debug class as the main argument in the connector and launches the VM
     * @return VirtualMachine
     */
    public VirtualMachine connectAndLaunchVM() throws IOException, IllegalConnectorArgumentsException, VMStartException {
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(JUNIT_TEST_RUNNER + " " + this.getTesterClass()+"#"+this.getTesterMethod());//xxxTest test
//      monitor.SingleJUnitTestRunner xxx.xxxTest#xxx
        arguments.get("options").setValue(String.format("-cp %s:%s:%s:%s:.",JUNIT_PATH,HAMCREST_PATH,
                this.getTesterClassPath(),this.testeeClassPath));
//      java -cp junit_path:Ha.jar:testerClassPath:testeeClassPath:. monitor.SingleXXXrunner monitor.XXXTest#test
        return launchingConnector.launch(arguments);
    }

    /**
     * Creates a request to prepare the debug class, add filter as the debug class and enables it
     * @param vm
     */
    public void enableClassPrepareRequest(VirtualMachine vm) {

        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
//        classPrepareRequest.addClassFilter("monitor.SingleJUnitTestRunner");
        classPrepareRequest.addClassFilter(testeeClass);
        classPrepareRequest.addCountFilter(1);
        classPrepareRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        classPrepareRequest.enable();
    }


    private void registerClassPrepareRequestForTester(VirtualMachine vm) {
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(SingleJUnitTestRunner.class.getName());
        classPrepareRequest.addCountFilter(1);
        classPrepareRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        classPrepareRequest.enable();
    }


    /**
     * Sets the break points at the line numbers mentioned in breakPointLines array
     * @param vm
     * @param event
     * @throws AbsentInformationException
     */
    public void setBreakPoints(VirtualMachine vm, ClassPrepareEvent event) throws AbsentInformationException {
        ClassType classType = (ClassType) event.referenceType();
        for(int lineNumber: breakPointLines) {
            Location location = classType.locationsOfLine(lineNumber).get(0);
            BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
            bpReq.enable();
        }
    }

    String getValueFromValue(LocatableEvent event, Value var){
        if(var instanceof ObjectReference){
            if(var instanceof ArrayReference){
                List<Value> array = ((ArrayReference) var).getValues();
                StringBuilder ret = new StringBuilder("[");
                for (int i = 0; i < array.size(); i++) {
                    if(i!=0){
                        ret.append(",");
                    }
                    ret.append(getValueFromValue(event, array.get(i)));
                }
                ret.append("]");
                return ret.toString();
            }else{
                ObjectReference ref = (ObjectReference) var;
                Method toString = ref.referenceType()
                        .methodsByName("toString", "()Ljava/lang/String;").get(0);
                try {
                    return ref.invokeMethod(event.thread(), toString, Collections.emptyList(), 0).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(var == null)
            return "";
        if(var.toString().equals("\0")||var.toString().contains("\u0000")) {
            if(var.type() instanceof CharType) {
                return "'0'";
            }
            return "";
        }
        if(var.toString().contains("\\")){
            return "";
        }
        if(var.type() instanceof CharType) {
            String a = String.format("'%s'", var);
//            System.out.println(a);
            return a;
        }
        if(var.type().toString().equals("char")){
//            System.out.println("csakjh");
            return "";
        }

        return var.toString().replace("\u0000","");
    }

    /**
     * Displays the visible variables
     * @param event
     * @throws IncompatibleThreadStateException
     * @throws AbsentInformationException
     */
    public void displayVariables(LocatableEvent event) throws IncompatibleThreadStateException, AbsentInformationException {
//        System.out.println(event.thread().frames().size());
        StackFrame stackFrame = event.thread().frame(0);
        if(stackFrame.location().toString().contains(testeeClass)) {
            Map<LocalVariable, Value> visibleVariables = stackFrame.getValues(stackFrame.visibleVariables());

            this.logger.add("Variables at "+stackFrame.location().toString() +  "\n");
//            System.out.println("Variables at " +stackFrame.location().toString() +  " > ");
            this.logger.add("[\n");
            for (Map.Entry<LocalVariable, Value> entry : visibleVariables.entrySet()) {
                this.logger.add("{");
                this.logger.add(String.format("\"name\":\"%s\",",entry.getKey().name()));
                if(entry.getValue() != null && !entry.getValue().type().toString().equals("null")) {
                    this.logger.add(String.format("\"type\":\"%s\",", entry.getValue().type()));
                    this.logger.add(String.format("\"value\":%s", getValueFromValue(event, entry.getValue())));
                }else{
                    this.logger.add(String.format("\"type\":\"%s\",", "null"));
                    this.logger.add(String.format("\"value\":%s", "null"));
                }
//                System.out.println(entry.getKey().name() + " = " + entry.getValue());
                this.logger.add("}\n");
            }
            this.logger.add("]\n");
        }
    }

    /**
     * Enables step request for a break point
     * @param vm
     * @param event
     */
    public void enableStepRequest(VirtualMachine vm, BreakpointEvent event) {
        //enable step request for last break point
        StepRequest stepRequest = vm.eventRequestManager().createStepRequest(event.thread(), StepRequest.STEP_LINE, StepRequest.STEP_OVER);
        stepRequest.enable();

    }

    public Method getMethodToMonitorFromType(ReferenceType referenceType){
        String className = referenceType.name();
        List<Method> methods = referenceType.methodsByName(this.getTesteeMethod());
        if(methods.size()==0){
            System.out.println("find no method");
            throw new IllegalStateException("Incorrect number of methods to monitor :" + methods.size());
        }
        return methods.get(0);
    }

    public void addBreakPointToFirstLineInMethod(VirtualMachine vm, Method method)throws AbsentInformationException {
        Set<Integer> lineNoSetAlready = new HashSet<>();

        List<Location> jdiLocation = method.allLineLocations();
        List<BreakpointRequest> requests = new LinkedList<>();
        for (Location location : jdiLocation) {
//            System.out.println(location.lineNumber());
            if (!lineNoSetAlready.contains(location.lineNumber())) {
                BreakpointRequest breakpointRequest = vm.eventRequestManager().createBreakpointRequest(location);
                breakpointRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                breakpointRequest.enable();
                lineNoSetAlready.add(location.lineNumber());
                requests.add(breakpointRequest);
                break;
            }
        }
        setBreakpointRequests(requests);
//        return null;
    }

    public void registerTestBoundaryBreakpoint(VirtualMachine vm, ReferenceType referenceType,String methodName){
        List<Method> methods = referenceType.methodsByName(methodName);
        if (methods.isEmpty() || methods.size() > 1)
            throw new IllegalStateException();

        Method testStartMethod = methods.get(0);
        try {
            List<Location> locations = testStartMethod.allLineLocations();
            Location location = locations.get(0);
            addBreakPointToFirstLineInMethod(vm,testStartMethod);

        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    public static void main(String[] args) throws Exception {
        boolean testAll = args[0].equals("Mode0");
        //args[6] = {testeeClass, testeeMethod, testerClassPath, testerClass, testerMethod}
        JDIDebugger debuggerInstance;
        if(testAll){
            debuggerInstance = new JDIDebugger(args[1],args[2],args[3],args[4],args[5]);
        }else{
            debuggerInstance = new JDIDebugger(args[1],args[2],args[3],args[4],args[5], args[6]);
        }
        VirtualMachine vm = null;
        boolean isBeginStep = false;

        try {
            vm = debuggerInstance.connectAndLaunchVM();
            debuggerInstance.enableClassPrepareRequest(vm);
            debuggerInstance.registerClassPrepareRequestForTester(vm);
            EventSet eventSet = null;
            long startTime = System.currentTimeMillis();
            long currentTime = System.currentTimeMillis();
            while ((eventSet = vm.eventQueue().remove()) != null && currentTime-startTime<10000) {
//                System.out.println("sss");
                for (Event event : eventSet) {
                    if (event instanceof VMStartEvent) {
//                        System.out.println("VM started");
                        eventSet.resume();
                    }else if (event instanceof VMDeathEvent) {
                        eventSet.resume();
                    }else if(event instanceof VMDisconnectEvent){
                        throw new VMDisconnectedException();
                    } else if (event instanceof ClassPrepareEvent){
                        ClassPrepareEvent classPrepareEvent = (ClassPrepareEvent) event;
                        ReferenceType referenceType = classPrepareEvent.referenceType();
                        if(referenceType.name().equals(debuggerInstance.getTesteeClass())){//monitor.xxxx
//                            System.out.println("yyyy");
                            if(!testAll) {
                                Method method = debuggerInstance.getMethodToMonitorFromType(referenceType);
                                debuggerInstance.addBreakPointToFirstLineInMethod(vm, method);
                            }
                        }
                        eventSet.resume();
                    }else if(event instanceof BreakpointEvent){
                        if(!isBeginStep) {
                            debuggerInstance.getBreakpointRequests().forEach(EventRequest::disable);
                            BreakpointEvent breakpointEvent = (BreakpointEvent) event;
//                        System.out.println(breakpointEvent.location());
                            debuggerInstance.displayVariables((BreakpointEvent) event);
                            debuggerInstance.getBreakpointRequests().forEach(EventRequest::enable);
                            debuggerInstance.enableStepRequest(vm, (BreakpointEvent) event);
                            isBeginStep = true;
                            eventSet.resume();
                        }else{
                            debuggerInstance.getBreakpointRequests().forEach(EventRequest::disable);
                            eventSet.resume();
                        }
                    }else if (event instanceof StepEvent) {
                        debuggerInstance.displayVariables((StepEvent) event);
                        eventSet.resume();
                    }else{
                        vm.resume();
                    }
//                    System.out.println("mmm");
                }
                currentTime = System.currentTimeMillis();
            }
    } catch (VMDisconnectedException e) {
            System.out.println("Virtual Machine is done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            assert vm != null;
            File logDir = new File(Paths.get(ROOT_PATH,"..","..","..","tmp").toString());
            File resultLog = new File(Paths.get(ROOT_PATH,"..","..","..", "tmp", "log.log").toString());
            File executeLog = new File(Paths.get(ROOT_PATH, "..", "..", "..", "tmp", "execute.log").toString());
            if(!logDir.exists()) {
                if(!logDir.mkdir()){
                    System.out.println("Failed to mkdir tmpDir!");
                }
            }
            if(!resultLog.exists()) {
                if(!resultLog.createNewFile()){
                    System.out.println("Failed to create log.log");
                }
            }
            if(executeLog.exists()){
                if(!executeLog.delete()){
                    System.out.println("Failed to delete execute.log");
                }
                System.out.println("Delete execute.log success!");
            }
            InputStreamReader read = new InputStreamReader(vm.process().getInputStream());
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(Paths.get(logDir.getPath(), "log.log").toString()));
            BufferedReader reader = new BufferedReader(read);
            BufferedWriter writer = new BufferedWriter(write);
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            FileWriter fw = null;
            try {

                fw = new FileWriter(Paths.get(logDir.getPath(), "execute.log").toString(), false);
                for (String log : debuggerInstance.getLogger()) {
                    fw.write(log);
                }
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fw != null)
                    try {
                        fw.flush();
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}
