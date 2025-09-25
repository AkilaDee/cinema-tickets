package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import static org.junit.jupiter.api.Assertions.*;

class TicketServiceImplTest {

    private TicketServiceImpl ticketService;

    @BeforeEach
    void setup() {
        ticketService = new TicketServiceImpl();
    }

    @Test
    void shouldThrowExceptionWhenAccountIdIsInvalid() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(0L, adult));
    }

    @Test
    void shouldThrowExceptionWhenNoAdultTicketForChild() {
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, child));
    }

    @Test
    void shouldThrowExceptionWhenNoAdultTicketForInfant() {
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, infant));
    }

    @Test
    void shouldThrowExceptionWhenMoreThan25TicketsPurchased() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);
        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, adult));
    }

    @Test
    void shouldProcessValidPurchase() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        // No exception should be thrown for a valid purchase
        assertDoesNotThrow(() ->
                ticketService.purchaseTickets(1L, adult, child, infant));
    }

    @Test
    void shouldThrowExceptionWhenZeroTicketsRequested() {
        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L));
    }

    @Test
    void calculateTotalPriceShouldReturnCorrectSum() {
        int totalPrice = ticketService.calculateTotalPrice(2, 3);
        assertEquals(2 * 25 + 3 * 15, totalPrice);
    }

    @Test
    void calculateTotalPriceWithZeroChildrenOrAdults() {
        assertEquals(0, ticketService.calculateTotalPrice(0, 0));
        assertEquals(50, ticketService.calculateTotalPrice(2, 0));
        assertEquals(30, ticketService.calculateTotalPrice(0, 2));
    }

    @Test
    void getTicketCountShouldReturnCorrectCount() {
        TicketTypeRequest adult1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest adult2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);

        TicketTypeRequest[] requests = {adult1, adult2, child};

        int adultCount = ticketService.getTicketCount(requests, TicketTypeRequest.Type.ADULT);
        int childCount = ticketService.getTicketCount(requests, TicketTypeRequest.Type.CHILD);
        int infantCount = ticketService.getTicketCount(requests, TicketTypeRequest.Type.INFANT);

        assertEquals(3, adultCount);
        assertEquals(3, childCount);
        assertEquals(0, infantCount);
    }

    @Test
    void getTicketCountWithEmptyArrayShouldReturnZero() {
        TicketTypeRequest[] requests = {};
        int count = ticketService.getTicketCount(requests, TicketTypeRequest.Type.ADULT);
        assertEquals(0, count);
    }

    @Test
    void validateTicketsShouldThrowForTooManyTickets() {
        InvalidPurchaseException exception = assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.validateTickets(10, 10, 6, 26)
        );
        assertEquals("The number of tickets must not exceed 25", exception.getMessage());
    }

    @Test
    void validateTicketsShouldThrowForChildOrInfantWithoutAdult() {
        InvalidPurchaseException exception = assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.validateTickets(0, 1, 1, 2)
        );
        assertEquals("Child/Infant tickets can not be purchased without an Adult ticket", exception.getMessage());
    }

    @Test
    void validateTicketsShouldPassForValidCombination() {
        assertDoesNotThrow(() -> ticketService.validateTickets(2, 3, 1, 6));
    }
}