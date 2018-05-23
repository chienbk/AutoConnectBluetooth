package chienbk.com.bluetoothnrfuart.model;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by ChienNV9 on 4/4/2018.
 */

public class Student implements Comparable<Student>{

    private String name;
    private String address;
    private int age;

    public Student(String name, String address, int age) {
        this.name = name;
        this.address = address;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int compareTo(@NonNull Student student) {

        int compare = student.getAge();
        return this.age - compare;
    }

    public static Comparator<Student> studentComparator = new Comparator<Student>() {
        @Override
        public int compare(Student student, Student t1) {
            return student.compareTo(t1);
        }
    };
}
