package br.com.kauedb.usb;

import javax.usb.*;
import java.util.List;

/**
 * Created by Kauê Q. Carbonari.
 */
public class UsbHandler {

    private UsbDevice device;

    public UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) return device;
            if (device.isUsbHub()) {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) {
                    this.device = device;
                    return device;
                }
            }
        }
        return null;
    }


    public byte[] sendRequest(UsbDevice device) throws UsbException {
        final UsbControlIrp irp = device.createUsbControlIrp(
                (byte) (UsbConst.REQUESTTYPE_DIRECTION_IN
                        | UsbConst.REQUESTTYPE_TYPE_STANDARD
                        | UsbConst.REQUESTTYPE_RECIPIENT_DEVICE),
                UsbConst.REQUEST_GET_CONFIGURATION,
                (short) 0,
                (short) 0
        );
        irp.setData(new byte[1]);
        device.syncSubmit(irp);
        return irp.getData();
    }


}
