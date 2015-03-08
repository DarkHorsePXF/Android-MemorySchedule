package com.example.memory;

public class MemBlock {
	public int parNum=0;//块号
	public int addr=0;	//地址
	public int size=0;	//内存块大小
	public int minSizeLeft=5;	//最小切割块
	public boolean isFree=true;	//是否被占用
	private JCB jcb;	//所调度的作业
	public MemBlock(int parNum,int addr,int size) {
		this.parNum=parNum;
		this.addr=addr;
		this.size=size;
	}
	public JCB getJcb() {
		return jcb;
	}
	public void setJcb(JCB jcb) {
		this.jcb = jcb;
	}
	
}
