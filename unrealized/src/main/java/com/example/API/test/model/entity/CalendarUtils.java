package com.example.API.test.model.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarUtils {
    Calendar calendar = Calendar.getInstance();

    final static String[] dayOfWeek = {"", "日", "一", "二", "三", "四", "五", "六"};

    private List<String> NationalHolidays = new ArrayList<>();

    public CalendarUtils() {
        // 初始化建立holiday
        getHolidays();
    }


    public List<String> getHolidays() {
        return getHolidays(getYear());
    }

    // 指定年分
    public List<String> getHolidays(int year) {
        if (NationalHolidays.size() == 0) {
            System.out.println("Holiday List is Empty, so init [" + year
                    + "] holiday data!");
            NationalHolidays = this.getYearOfHolidayList(year);
        }
        return NationalHolidays;
    }

    public void setYear(int year) {
        NationalHolidays = this.getYearOfHolidayList(year);
    }

    public void addHoliday(String date) {
        if (!NationalHolidays.contains(date)) {
            this.NationalHolidays.add(date);
        }
    }

    public void removeHoliday(String date) {
        this.NationalHolidays.remove(date);
    }

    public String rightNow() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    public int getYear(Calendar cal) {
        return cal.get(Calendar.YEAR);
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getMonth(Calendar cal) {
        return cal.get(Calendar.MONTH) + 1;
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getDayOfMonth(Calendar cal) {
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public int getDayOfMonth() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    public int getSecond() {
        return calendar.get(Calendar.SECOND);
    }


    public boolean isHoliday(Calendar cal) {

        if (null == cal) {
            return false;
        }
        // 1.六日
        String dayofweek = getDayOfWeek(cal);
        if (dayofweek.equals("六") || dayofweek.equals("日")) {
            return true;
        }

        // 2.國定假日
        return NationalHolidays.contains(getDate(cal));
    }

    private boolean isHoliday() {
        return isHoliday(calendar);
    }

    //判斷星期幾
    public String getDayOfWeek(Calendar cal) {
        if (cal == null) {
            return dayOfWeek[calendar.get(Calendar.DAY_OF_WEEK)];
        }
        return dayOfWeek[cal.get(Calendar.DAY_OF_WEEK)];
    }


    //取出該年有多少天例假日
    private List<String> getYearOfHolidayList(int year) {
        Calendar cal = Calendar.getInstance();
        boolean thisYear = true;
        int d = 1;
        List<String> holidayList = new LinkedList<String>();
        while (thisYear) {
            cal.clear();
            cal.set(year, 0, d);
            getYear(cal);
            if (getYear(cal) != year) {
                break;
            }
            if (isHoliday(cal)) {
                holidayList.add(getDate(cal));
            }
            d++;
        }
        System.out.println(year + "年今年共 " + (d - 1) + "天");
// System.out.println(holidayList);
        return holidayList;

    }

    private String getDate(Calendar cal) {
        return getYear(cal) + "/" + getMonth(cal) + "/" + getDayOfMonth(cal);
    }

    public void init(CalendarUtils cu) throws ParseException {
        cu.addHoliday("2022/1/31");
        cu.addHoliday("2022/2/1");
        cu.addHoliday("2022/2/2");
        cu.addHoliday("2022/2/3");
        cu.addHoliday("2022/2/4");
        cu.addHoliday("2022/2/28");
        cu.addHoliday("2022/4/4");
        cu.addHoliday("2022/4/5");
        cu.addHoliday("2022/5/1");
        cu.addHoliday("2022/6/3");
        cu.addHoliday("2022/9/9");
        cu.addHoliday("2022/10/10");
        System.out.println(cu.getHolidays());
    }
}
