/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed WITHOUT ANY WARRANTY; and without the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt 
 * or write to:
 * 
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 */

package com.jaspersoft.jasperserver.ws.scheduling;

/**
 * JobCalendarTrigger.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */
public class JobCalendarTrigger  extends com.jaspersoft.jasperserver.ws.scheduling.JobTrigger  implements java.io.Serializable {
    private java.lang.String minutes;

    private java.lang.String hours;

    private com.jaspersoft.jasperserver.ws.scheduling.CalendarDaysType daysType;

    private int[] weekDays;

    private java.lang.String monthDays;

    private int[] months;

    public JobCalendarTrigger() {
    }

    public JobCalendarTrigger(
           long id,
           int version,
           java.lang.String timezone,
           java.util.Calendar startDate,
           java.util.Calendar endDate,
           java.lang.String minutes,
           java.lang.String hours,
           com.jaspersoft.jasperserver.ws.scheduling.CalendarDaysType daysType,
           int[] weekDays,
           java.lang.String monthDays,
           int[] months) {
        super(
            id,
            version,
            timezone,
            startDate,
            endDate);
        this.minutes = minutes;
        this.hours = hours;
        this.daysType = daysType;
        this.weekDays = weekDays;
        this.monthDays = monthDays;
        this.months = months;
    }


    /**
     * Gets the minutes value for this JobCalendarTrigger.
     * 
     * @return minutes
     */
    public java.lang.String getMinutes() {
        return minutes;
    }


    /**
     * Sets the minutes value for this JobCalendarTrigger.
     * 
     * @param minutes
     */
    public void setMinutes(java.lang.String minutes) {
        this.minutes = minutes;
    }


    /**
     * Gets the hours value for this JobCalendarTrigger.
     * 
     * @return hours
     */
    public java.lang.String getHours() {
        return hours;
    }


    /**
     * Sets the hours value for this JobCalendarTrigger.
     * 
     * @param hours
     */
    public void setHours(java.lang.String hours) {
        this.hours = hours;
    }


    /**
     * Gets the daysType value for this JobCalendarTrigger.
     * 
     * @return daysType
     */
    public com.jaspersoft.jasperserver.ws.scheduling.CalendarDaysType getDaysType() {
        return daysType;
    }


    /**
     * Sets the daysType value for this JobCalendarTrigger.
     * 
     * @param daysType
     */
    public void setDaysType(com.jaspersoft.jasperserver.ws.scheduling.CalendarDaysType daysType) {
        this.daysType = daysType;
    }


    /**
     * Gets the weekDays value for this JobCalendarTrigger.
     * 
     * @return weekDays
     */
    public int[] getWeekDays() {
        return weekDays;
    }


    /**
     * Sets the weekDays value for this JobCalendarTrigger.
     * 
     * @param weekDays
     */
    public void setWeekDays(int[] weekDays) {
        this.weekDays = weekDays;
    }


    /**
     * Gets the monthDays value for this JobCalendarTrigger.
     * 
     * @return monthDays
     */
    public java.lang.String getMonthDays() {
        return monthDays;
    }


    /**
     * Sets the monthDays value for this JobCalendarTrigger.
     * 
     * @param monthDays
     */
    public void setMonthDays(java.lang.String monthDays) {
        this.monthDays = monthDays;
    }


    /**
     * Gets the months value for this JobCalendarTrigger.
     * 
     * @return months
     */
    public int[] getMonths() {
        return months;
    }


    /**
     * Sets the months value for this JobCalendarTrigger.
     * 
     * @param months
     */
    public void setMonths(int[] months) {
        this.months = months;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof JobCalendarTrigger)) return false;
        JobCalendarTrigger other = (JobCalendarTrigger) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.minutes==null && other.getMinutes()==null) || 
             (this.minutes!=null &&
              this.minutes.equals(other.getMinutes()))) &&
            ((this.hours==null && other.getHours()==null) || 
             (this.hours!=null &&
              this.hours.equals(other.getHours()))) &&
            ((this.daysType==null && other.getDaysType()==null) || 
             (this.daysType!=null &&
              this.daysType.equals(other.getDaysType()))) &&
            ((this.weekDays==null && other.getWeekDays()==null) || 
             (this.weekDays!=null &&
              java.util.Arrays.equals(this.weekDays, other.getWeekDays()))) &&
            ((this.monthDays==null && other.getMonthDays()==null) || 
             (this.monthDays!=null &&
              this.monthDays.equals(other.getMonthDays()))) &&
            ((this.months==null && other.getMonths()==null) || 
             (this.months!=null &&
              java.util.Arrays.equals(this.months, other.getMonths())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getMinutes() != null) {
            _hashCode += getMinutes().hashCode();
        }
        if (getHours() != null) {
            _hashCode += getHours().hashCode();
        }
        if (getDaysType() != null) {
            _hashCode += getDaysType().hashCode();
        }
        if (getWeekDays() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getWeekDays());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getWeekDays(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMonthDays() != null) {
            _hashCode += getMonthDays().hashCode();
        }
        if (getMonths() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMonths());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMonths(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
