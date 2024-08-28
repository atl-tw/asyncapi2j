package enginetest;

import com.google.common.util.concurrent.Uninterruptibles;
import com.thoughtworks.asyncapi.serde.SerializerFactory;
import com.thoughtworks.asyncapi.serde.impl.JacksonSerDe;
import enginetest.model.OrderDetails;
import enginetest.model.PreInquiryRequest;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.google.common.truth.Truth.assertThat;

public class Test implements Runnable {

  @Override
  public void run() {

    try {
      var client = new MqttClient("tcp://localhost:1883", "test", new MemoryPersistence());
      client.connect();
      var myclient = new MyClient(client, new SerializerFactory(Map.of("application/json", new JacksonSerDe())));

      AtomicReference<PreInquiryRequest> ref = new AtomicReference<>();
      myclient.subscribePreInquiryRequest(ref::set);
      var request= new PreInquiryRequest();
      var orderDetails = new OrderDetails();
      orderDetails.setOrderNumber("1234");
      request.setOrder(orderDetails);
      System.out.println("Publishing "+request);
      myclient.publishPreInquiryRequest(request);
      Uninterruptibles.sleepUninterruptibly(500, java.util.concurrent.TimeUnit.MILLISECONDS);
      System.out.println("Received "+ref.get());
      assertThat(ref.get().getOrder().getOrderNumber()).isEqualTo("1234");

    } catch (MqttException e) {
      throw new RuntimeException(e);
    }

  }
}
class MyClient extends MQTTClient {

  public MyClient(IMqttClient client, SerializerFactory serializerFactory) {
    super(client, serializerFactory);
  }

  public void publishPreInquiryRequest(PreInquiryRequest payload) {
    super.publishPreInquiryRequest(payload);
  }

  public void subscribeWeaklyPreInquiryRequest(Consumer<PreInquiryRequest> consumer) {
    super.subscribeWeaklyPreInquiryRequest(consumer);
  }

  public void subscribePreInquiryRequest(Consumer<PreInquiryRequest> consumer) {
    super.subscribePreInquiryRequest(consumer);
  }
}
