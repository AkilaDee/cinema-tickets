package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;


public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private final TicketPaymentService paymentService;
    private final SeatReservationService reservationService;

    public TicketServiceImpl() {
        this.paymentService = new TicketPaymentServiceImpl();
        this.reservationService = new SeatReservationServiceImpl();
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        if (accountId == null || accountId <= 0){
            throw new InvalidPurchaseException("Invalid account ID");
        }

        if (ticketTypeRequests == null || ticketTypeRequests.length == 0){
            throw new InvalidPurchaseException("No tickets are given");
        }

        int adultCount = getTicketCount(ticketTypeRequests, TicketTypeRequest.Type.ADULT);
        int childCount = getTicketCount(ticketTypeRequests, TicketTypeRequest.Type.CHILD);
        int infantCount = getTicketCount(ticketTypeRequests, TicketTypeRequest.Type.INFANT);
        int totalTickets = adultCount + childCount + infantCount;

        validateTickets(adultCount, childCount, infantCount, totalTickets);

        int totalPrice = calculateTotalPrice(adultCount, childCount);
        int seatsToReserve = adultCount + childCount;

        paymentService.makePayment(accountId, totalPrice);
        reservationService.reserveSeat(accountId, seatsToReserve);
    }

    private void validateTickets(int adultCount, int childCount, int infantCount, int totalTickets){
        if(totalTickets > 25){
            throw new InvalidPurchaseException("The number of tickets must not exceed 25");
        }

        if((childCount > 0 || infantCount > 0 ) && adultCount == 0){
            throw new InvalidPurchaseException("Chiild/Infant tickets can not be purchased without an Adult ticket");
        }
    }

    private int calculateTotalPrice(int adultCount, int childCount){
        return (adultCount * 25) + (childCount * 15);
    }

    private int getTicketCount(TicketTypeRequest[] requests, TicketTypeRequest.Type type){
        int count = 0;
        for(TicketTypeRequest req : requests){
            if(req.getTicketType() == type){
                count += req.getNoOfTickets();
            }
        }
        return count;
    }

}
