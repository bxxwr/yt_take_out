package com.dy.utils;
import com.dy.entity.Employee;


public class EmpThreadLocal {
    private EmpThreadLocal(){}

    private static final ThreadLocal<Employee> LOCAL = new ThreadLocal<>();

    public static void put(Employee Employee){
        LOCAL.set(Employee);
    }
    public static Employee get(){
        return LOCAL.get();
    }
    public static void remove(){
        LOCAL.remove();
    }
}
