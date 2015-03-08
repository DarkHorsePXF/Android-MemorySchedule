package com.example.memory;

import android.widget.Button;

public class JCB {


	/**
	 * 作业控制块
	 * @author pxf
	 * @date 2014-11-30
	 */
	private String jobName;
	private int timeNeeded;
	private int waitingTime;
	private int memory=0;
	private boolean run=false;
	private long startWaitingTime;
	private int address=0;
	private double response;
	public Button btn;
	public JCB(String jobName,int memory,Button btn){
		this.jobName=jobName;
		this.memory=memory;
		this.btn=btn;
	}
	/**
	 * @return the jobName
	 */
	public String getName() {
		return jobName;
	}
	/**
	 * @param jobName the jobName to set
	 */
	public void setName(String jobName) {
		this.jobName = jobName;
	}
	/**
	 * @return the timeNeeded
	 */
	public int getTimeNeeded() {
		return timeNeeded;
	}
	/**
	 * @param timeNeeded the timeNeeded to set
	 */
	public void setTimeNeeded(int timeNeeded) {
		this.timeNeeded = timeNeeded;
	}
	/**
	 * @return the priority
	 */
	public int getWaitingtime() {
		return waitingTime;
		
	}
	/**
	 * @param priority the priority to set
	 */
	public void setWaitingtime(long currentTime) {
		this.waitingTime = (int)(currentTime-startWaitingTime);
		setResponse();
	}
	/**
	 * @return the run
	 */
	public boolean isRun() {
		return run;
	}
	/**
	 * @param run the run to set
	 */
	public void setRun(boolean run) {
		this.run = run;
	}
	public int getMemory() {
		return memory;
	}
	public void setMemory(int memory) {
		this.memory = memory;
	}
	public long getStartWaitingTime() {
		return startWaitingTime;
	}
	public void setStartWaitingTime(long startWaitingTime) {
		this.startWaitingTime = startWaitingTime;
		
	}
	public double getResponse() {
		return response;
	}
	public void setResponse() {
		this.response = ((waitingTime+timeNeeded)/timeNeeded)/100;
	}
	public int getAddress() {
		return address;
	}
	public void setAddress(int address) {
		this.address = address;
	}


	

}
