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
}