package br.com.kauedb.usb;

import org.junit.Test;

import javax.usb.*;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class UsbHandlerTest {

    @Test
    public void shouldFindPrinter() throws Exception {

        final UsbHandler usbHandler = new UsbHandler();

        final UsbHub hub = UsbHostManager.getUsbServices().getRootUsbHub();

        final UsbDevice device = usbHandler.findDevice(hub, (short) 5732, (short) 410);

        assertThat(device, notNullValue());
        assertThat(device.getProductString(), is("ARGOX OS-214 plus PPLA "));
    }

    @Test
    public void shouldSendRequestAndGetConfigurationNumber() throws Exception {

        final UsbHandler usbHandler = new UsbHandler();

        final UsbHub hub = UsbHostManager.getUsbServices().getRootUsbHub();
        final UsbDevice device = usbHandler.findDevice(hub, (short) 5732, (short) 410);

        byte[] data = usbHandler.sendRequest(device);
        assertThat(data, notNullValue());
        assertThat(data, is(new byte[]{device.getActiveUsbConfigurationNumber()}));
    }

    @Test
    public void shouldGetInterface() throws Exception {

        final UsbHandler usbHandler = new UsbHandler();
        final UsbHub hub = UsbHostManager.getUsbServices().getRootUsbHub();
        final UsbDevice device = usbHandler.findDevice(hub, (short) 5732, (short) 410);


        final UsbConfiguration configuration = device.getActiveUsbConfiguration();
        assertThat(configuration.getUsbInterfaces(), notNullValue());
        assertThat(configuration.getUsbInterfaces().size(), is(1));
        final UsbInterface usbInterface = (UsbInterface) configuration.getUsbInterfaces().iterator().next();
        usbInterface.claim(new UsbInterfacePolicy() {
            @Override
            public boolean forceClaim(UsbInterface usbInterface) {
                return true;
            }
        }); // claim without driver
        try {

        } finally {
            usbInterface.release();
        }
    }

    @Test
    public void shouldGetEndpoint() throws Exception {

        final UsbHandler usbHandler = new UsbHandler();
        final UsbHub hub = UsbHostManager.getUsbServices().getRootUsbHub();
        final UsbDevice device = usbHandler.findDevice(hub, (short) 5732, (short) 410);


        final UsbConfiguration configuration = device.getActiveUsbConfiguration();
        assertThat(configuration.getUsbInterfaces(), notNullValue());
        assertThat(configuration.getUsbInterfaces().size(), is(1));
        final UsbInterface usbInterface = (UsbInterface) configuration.getUsbInterfaces().iterator().next();
        usbInterface.claim(new UsbInterfacePolicy() {
            @Override
            public boolean forceClaim(UsbInterface usbInterface) {
                return true;
            }
        }); // claim without driver
        try {

            assertThat(usbInterface.getUsbEndpoints(), notNullValue());
            assertThat(usbInterface.getUsbEndpoints().size(), is(2));

            final UsbEndpoint endpoint = usbInterface.getUsbEndpoint((byte) 1);
            assertThat(endpoint.getUsbEndpointDescriptor().bEndpointAddress(), is((byte) 1));

//            for (UsbEndpoint usbEndpoint : (List<UsbEndpoint>) usbInterface.getUsbEndpoints()) {
//                System.out.println(usbEndpoint.getDirection());
//                System.out.println(usbEndpoint.getUsbEndpointDescriptor().bEndpointAddress());
//            }

        } finally {
            usbInterface.release();
        }

    }

    @Test
    public void shouldSendCommand() throws Exception {

        final UsbHandler usbHandler = new UsbHandler();
        final UsbHub hub = UsbHostManager.getUsbServices().getRootUsbHub();
        final UsbDevice device = usbHandler.findDevice(hub, (short) 5732, (short) 410);


        final UsbConfiguration configuration = device.getActiveUsbConfiguration();
        assertThat(configuration.getUsbInterfaces(), notNullValue());
        assertThat(configuration.getUsbInterfaces().size(), is(1));
        final UsbInterface usbInterface = (UsbInterface) configuration.getUsbInterfaces().iterator().next();
        usbInterface.claim(new UsbInterfacePolicy() {
            @Override
            public boolean forceClaim(UsbInterface usbInterface) {
                return true;
            }
        }); // claim without driver
        UsbPipe usbPipe1 = null;
//        UsbPipe usbPipe2 = null;
        try {

            assertThat(usbInterface.getUsbEndpoints(), notNullValue());
            assertThat(usbInterface.getUsbEndpoints().size(), is(2));

            // System.out.println(((UsbEndpoint) usbInterface.getUsbEndpoints().get(1)).getUsbEndpointDescriptor().bEndpointAddress());

            final UsbEndpoint usbEndpoint1 = usbInterface.getUsbEndpoint((byte) 1);
            // final UsbEndpoint usbEndpoint2 = usbInterface.getUsbEndpoint((byte) -126);
            assertThat(usbEndpoint1.getUsbEndpointDescriptor().bEndpointAddress(), is((byte) 1));
            // assertThat(usbEndpoint2.getUsbEndpointDescriptor().bEndpointAddress(), is((byte) -126));

            // usbPipe2 = usbEndpoint2.getUsbPipe();
            // usbPipe2.open();

            usbPipe1 = usbEndpoint1.getUsbPipe();
            usbPipe1.open();

            final StringBuilder builder = new StringBuilder("\u0002O0220\n")
                    .append("\u0002M3000\n")
                    .append("\u0002c0000\n")
                    .append("\u0002f320\n")
                    .append("\u0002e\n")
                    .append("\u0002L\n")
                    .append("A2\n")
                    .append("C0000\n")
                    .append("D11\n")
                    .append("H13\n")
                    .append("SC\n")
                    .append("PC\n")
                    .append("R0000\n")
                    .append("z\n")
                    .append("^01\n")
                    .append("191200501300014Marcelo Maico\n")
                    .append("191200501300015Marcelo Maico\n")
                    .append("191200400950015Kohen Sistemas\n")
                    .append("141100000700014Analista\n")
                    .append("141100000100014\n")
                    .append("1e4202000260240B8837738\n")
                    .append("1200000001002408837738-Fornecedor\n")
                    .append("Q0001\n")
                    .append("E\n");


            final byte[] bytes = builder.toString().getBytes();

            final UsbPipeListener listener = new UsbPipeListener() {
                @Override
                public void errorEventOccurred(UsbPipeErrorEvent event) {
                    System.out.println(event);
                    System.out.println(event.getUsbException());
                }

                @Override
                public void dataEventOccurred(UsbPipeDataEvent event) {
                    System.out.println(event);
                    for (byte b : event.getData()) {
                        System.out.printf("%d", b);
                    }
//                    System.out.println(new String(event.getData()));
                }
            };

            usbPipe1.addUsbPipeListener(listener);
//            usbPipe2.addUsbPipeListener(listener);


            final int sent1 = usbPipe1.syncSubmit(bytes);
            System.out.println(sent1);

        } finally {
            if (usbPipe1 != null) {
                usbPipe1.close();
            }
//            if (usbPipe2 != null) {
//                usbPipe2.close();
//            }
            usbInterface.release();
        }

    }
}