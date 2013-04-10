package com.tgx.tina.android.plugin.contacts.calllog;

import com.tgx.tina.android.plugin.contacts.base.ContactTask;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog.Calls;
import base.tina.core.task.infc.ITaskProgress.TaskProgressType;


public class CallLogReadTask
        extends
        ContactTask
{
	
	public CallLogReadTask(Context context) {
		super(context);
	}
	
	@Override
	public int getSerialNum() {
		return SerialNum;
	}
	
	public final static int SerialNum          = SerialDomain + 10;
	
	final String[]          PROJECTION_STRINGS = {
	        Calls.NUMBER,//0
	        Calls.DATE,//1
	        Calls.DURATION,//2
	        Calls.TYPE,//3
	        Calls.NEW,//4
	        Calls._ID,//5
	        Calls.CACHED_NAME
	                                           //6
	                                           };
	final static int        MAX_READ           = 500;
	
	@Override
	public void run() throws Exception {
		CallLogProfile profile = null;
		Cursor callsCursor = context.getContentResolver().query(Calls.CONTENT_URI, PROJECTION_STRINGS, null, null, Calls.DEFAULT_SORT_ORDER);
		if (callsCursor != null) try
		{
			CallPack profilePack = new CallPack();
			int count = 0;
			while (callsCursor.moveToNext() && count < MAX_READ)
			{
				String phoneNum = callsCursor.getString(0);
				String cachedName = callsCursor.getString(6);
				if (profile == null || !profile.phoneNum.equals(phoneNum))
				{
					profile = new CallLogProfile(phoneNum, cachedName);
					profilePack.addProfile(profile);
				}
				profile.addEntry(callsCursor.getLong(2), callsCursor.getLong(1), callsCursor.getInt(3));
				count++;
			}
			commitResult(profilePack, CommitAction.WAKE_UP);
		}
		catch (Exception e)
		{
			//#debug warn
			e.printStackTrace();
			if (progress != null) progress.finishProgress(TaskProgressType.error);
		}
		finally
		{
			callsCursor.close();
		}
	}
}