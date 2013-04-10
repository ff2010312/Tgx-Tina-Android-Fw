package base.tina.external.io.net.socket;

import java.nio.channels.ClosedChannelException;

import base.tina.external.io.IoFilter;
import base.tina.external.io.IoSession;
import base.tina.external.io.IoTask;


public class SocketTask
        extends
        IoTask<NioSocketICon>
{
	public final static int SerialNum = SerialDomain + 1;
	
	@Override
	public final int getSerialNum() {
		return SerialNum;
	}
	
	public SocketTask(String url, IoFilter ioFilter) {
		super(url, ioFilter);
	}
	
	public SocketTask(IoSession<NioSocketICon> ioSession, IoFilter ioFilter) {
		super(ioSession, ioFilter);
	}
	
	public SocketTask(IoSession<NioSocketICon> ioSession) {
		super(ioSession);
	}
	
	@Override
	public void initTask() {
		isProxy = ioSession != null;
		super.initTask();
	}
	
	@Override
	public void run() throws Exception {
		if (ioSession != null)
		{
			if (ioSession.status.equals(IoSession.Status.UNREALIZED) || ioSession.status.equals(IoSession.Status.INVAILD)) throw new IllegalStateException("iosession is invaild!");
			if (ioSession.disconnect.get()) throw new ClosedChannelException();
			LSocketTask lSocketTask = LSocketTask.open();
			lSocketTask.offerWrite(this);
			if (!lSocketTask.isInit() && !scheduleService.requestService(lSocketTask, true, getListenSerial())) throw new IllegalStateException("LSocketTask is invalid");
			else lSocketTask.wakeUp();
		}
		else
		{
			CSocketTask cSocketTask = new CSocketTask(url, ioFilterChain);
			scheduleService.requestService(cSocketTask, true, getListenSerial());
		}
	}
}
