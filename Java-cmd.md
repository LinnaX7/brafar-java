# Java cmd

* 编译testee类

  ```shell
  javac xxx.java
  ```

* 编译测试tester类

  ```shell
  javac -cp %s[junit_path]:%s[testee_class_path]:. %s[tester_java]
  ```

* Run tester

  ```shell
  cd test_class_path[eg '/Users/seg_zt/Desktop/research-program-grading/grading_script/COMP2021/Assignment4ForGrading/test']
  
  java -cp %s[junit_path]:%s[hamcrest_path]:%s[testee_class_path]: org.junit.runner.JunitCore %s[package_head + tester_java]
  ```

* Run JDIDebugger

  * 编译所有的代码需要加上一些依赖tools.jar，junit.jar，还需要加上options -g

  ```shell
  java -g -cp  /Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home/lib/tools.jar:%s[junit_path]%s[testee_class_path]:. monitor/*.java
  ```

  
  * 运行JDIDugger代码

  ```shell
  java -cp "/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home/lib/tools.jar:." monitor.JDIDebugger
  ```

运行我的代码的命令行(在src/main/java目录下输入)参考如下：

* 编译所有的java文件

```shell
javac -g -cp /Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home/lib/tools.jar:../../../testlib/junit-4.13.2.jar:. monitor/*.java
```

* 运行JDIExampleDebugger， monitor.JDIExampleDebuggeeTest monitor.JDIExampleDebuggee print是3个参数

```
java -cp "/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home/lib/tools.jar:." monitor.JDIDebugger monitor.JDIExampleDebuggeeTest monitor.JDIExampleDebuggee print
```

 

JDI可以参考

https://blog.csdn.net/JimFire/article/details/120174611