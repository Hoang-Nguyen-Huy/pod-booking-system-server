package com.swp.PodBookingSystem.service;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.Method;
import biweekly.property.RecurrenceRule;
import biweekly.util.Duration;
import biweekly.util.Frequency;
import com.swp.PodBookingSystem.dto.request.CalendarRequest;
import com.swp.PodBookingSystem.dto.respone.Order.OrderManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailFullInfoResponse;
import com.swp.PodBookingSystem.entity.OrderDetail;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.*;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SendEmailService {
    JavaMailSender javaMailSender;

    @NonFinal
    @Value("${spring.mail.username}")
    String fromEmailId;

    @Autowired
    OrderDetailService orderDetailService;

    public void sendEmail(String recipient, String body, String subject) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromEmailId);
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(body, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendMailTemplate(String recipient, OrderManagementResponse order, String subject) throws MessagingException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        ClassPathResource resource = new ClassPathResource("templates/emailTemplate.html");
        String content;
        try (var inputStream = resource.getInputStream()) {
            content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        var roomHaveAmenities = order.getOrderDetails().stream()
                .filter(od -> !od.getAmenities().isEmpty())
                .collect(Collectors.toList());
        double totalPriceRoom = order.getOrderDetails().stream()
                .mapToDouble(orderDetail -> orderDetail.getRoomPrice())
                .sum();
        double totalPriceAmenity = order.getOrderDetails().stream()
                .mapToDouble(orderDetail -> orderDetail.getAmenities().stream()
                        .mapToDouble(amenity -> amenity.getPrice() * amenity.getQuantity())
                        .sum()
                )
                .sum();

        double priceBeforeDiscount = totalPriceRoom + totalPriceAmenity;
        double discountPercentage = order.getOrderDetails().get(0).getServicePackage().getDiscountPercentage();
        double finalPrice = priceBeforeDiscount * (1 - discountPercentage / 100);
        int integerAmount = (int) Math.round(finalPrice);
        String formattedAmount = String.format("%,d", integerAmount).replace(",", ".");
        String status;
        if (order.getOrderDetails().getFirst().getStatus().equals("Successfully")) {
            status = "Đã thanh toán";
        } else if (order.getOrderDetails().getFirst().getStatus().equals("Rejected")) {
            status = "Đã hủy";
        } else {
            status = "Đang chờ xử lí";
        }

        content = content.replace("{{orderId}}", order.getId())
                .replace("{{orderStartTime}}", order.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")))
                .replace("{{roomName}}", order.getOrderDetails().getFirst().getRoomTypeName())
                .replace("{{status}}", status)
                .replace("{{amenity}}", roomHaveAmenities.isEmpty() ? "Không có" : "Có")
                .replace("{{totalPrice}}", formattedAmount + " VND");

        helper.setFrom(fromEmailId);
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(content, true);

        javaMailSender.send(mimeMessage);
        log.info("Send email successfully");
    }

    public void sendMailAmenityOrder(String recipient, OrderDetailFullInfoResponse order, String subject) throws MessagingException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        ClassPathResource resource = new ClassPathResource("templates/emailTemplate.html");
        String content;
        try (var inputStream = resource.getInputStream()) {
            content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        var roomHaveAmenities = order.getAmenities().stream().map(amenity -> amenity.getName()).collect(Collectors.toList());

        double totalPriceAmenity = order.getAmenities().stream()
                .mapToDouble(amenity -> amenity.getPrice() * amenity.getQuantity())
                .sum();
        double finalPrice = totalPriceAmenity * (1 - order.getServicePackage().getDiscountPercentage() / 100);

        int integerAmount = (int) Math.round(finalPrice);
        String formattedAmount = String.format("%,d", integerAmount).replace(",", ".");
        content = content.replace("{{orderId}}", order.getId())
                .replace("{{orderStartTime}}", order.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")))
                .replace("{{roomName}}", order.getRoomName())
                .replace("{{status}}", order.getStatus())
                .replace("{{amenity}}", roomHaveAmenities.isEmpty() ? "Không có" : String.join(",", roomHaveAmenities))
                .replace("{{totalPrice}}", formattedAmount + " VND");

        helper.setFrom(fromEmailId);
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(content, true);

        javaMailSender.send(mimeMessage);
        log.info("Send email successfully");
    }

    public void sendCalenderInvite(CalendarRequest calenderDto) throws IOException, MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(calenderDto.getTo()));
        mimeMessage.setSubject(calenderDto.getSubject());
        mimeMessage.setFrom(fromEmailId);
        MimeMultipart mimeMultipart = new MimeMultipart("mixed");
        mimeMultipart.addBodyPart(createCalenderMimeBody(calenderDto));

        mimeMessage.setContent(mimeMultipart);
        javaMailSender.send(mimeMessage);

    }

    private BodyPart createCalenderMimeBody(CalendarRequest calenderDto) throws IOException, MessagingException {
        MimeBodyPart calenderBody = new MimeBodyPart();

        final DataSource source = new ByteArrayDataSource(createCal(calenderDto), "text/calender; charset=UTF-8");
        calenderBody.setDataHandler(new DataHandler(source));
        calenderBody.setHeader("Content-Type", "text/calendar; charset=UTF-8; method=REQUEST");

        return calenderBody;
    }

    private String createCal(CalendarRequest calenderDto) throws AddressException {
        ICalendar ical = new ICalendar();
        ical.addProperty(new Method(Method.REQUEST));

        VEvent event = new VEvent();
        event.setSummary(calenderDto.getSummary());
        event.setDescription(calenderDto.getDescription());
        event.setDateStart(getStartDate(calenderDto.getEventDateTime()));
        event.setOrganizer(String.valueOf(new InternetAddress(fromEmailId)));
        event.setDuration(new Duration.Builder()
                .hours(2)
                .build());
        ical.addEvent(event);
        return Biweekly.write(ical).go();
    }

    private Date getStartDate(LocalDateTime eventDateTime) {
        Instant instant = eventDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        return Date.from(instant);
    }

    @Scheduled(cron = "0 0 8 * * ?") //auto send mail at 8AM
    public void sendMailReminder() throws MessagingException {
        LocalDate toDay = LocalDate.now();
        List<OrderDetail> orders = orderDetailService.getNextDayBookings(toDay);

        for (OrderDetail orderDetail : orders) {
            String email = orderDetail.getCustomer().getEmail();
            String subject = "Room Booking Reminder";
            String text = "Dear " + orderDetail.getCustomer().getName() + ",\n\n"
                    + "This is a reminder that you have a booking for room " + orderDetail.getRoom().getName() + " tomorrow.\n"
                    + "Start Time: " + orderDetail.getStartTime() + "\n"
                    + "End Time: " + orderDetail.getEndTime() + "\n"
                    + "Price: $" + orderDetail.getPriceRoom() + "\n\n"
                    + "Thank you for choosing our service!";

            this.sendEmail(email, text, subject);
        }


    }

}
