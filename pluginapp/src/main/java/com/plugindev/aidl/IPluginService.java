/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\as_ws\\Android_Plug_In_Dev\\pluginaidl\\src\\main\\aidl\\com\\plugindev\\aidl\\IPluginService.aidl
 */
package com.plugindev.aidl;
public interface IPluginService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements IPluginService
{
private static final String DESCRIPTOR = "com.plugindev.aidl.IPluginService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.plugindev.aidl.IPluginService interface,
 * generating a proxy if needed.
 */
public static IPluginService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof IPluginService))) {
return ((IPluginService)iin);
}
return new Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_doOperate:
{
data.enforceInterface(DESCRIPTOR);
java.util.List _arg0;
ClassLoader cl = (ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readArrayList(cl);
this.doOperate(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements IPluginService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void doOperate(java.util.List channels) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeList(channels);
mRemote.transact(Stub.TRANSACTION_doOperate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_doOperate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void doOperate(java.util.List channels) throws android.os.RemoteException;
}
