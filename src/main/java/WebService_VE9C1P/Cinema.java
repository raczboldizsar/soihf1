package WebService_VE9C1P;

import java.util.ArrayList;
import java.util.HashMap;

import javax.jws.WebService;

import seatreservation.ArrayOfSeat;
import seatreservation.CinemaException;
import seatreservation.ICinema;
import seatreservation.ICinemaBuyCinemaException;
import seatreservation.ICinemaGetAllSeatsCinemaException;
import seatreservation.ICinemaGetSeatStatusCinemaException;
import seatreservation.ICinemaInitCinemaException;
import seatreservation.ICinemaLockCinemaException;
import seatreservation.ICinemaReserveCinemaException;
import seatreservation.ICinemaUnlockCinemaException;
import seatreservation.Seat;
import seatreservation.SeatStatus;

@WebService(name = "CinemaService", 
			portName = "ICinema_HttpSoap11_Port", 
			targetNamespace = "http://www.iit.bme.hu/soi/hw/SeatReservation", 
			endpointInterface = "seatreservation.ICinema", 
			wsdlLocation = "WEB-INF/wsdl/SeatReservation.wsdl")
public class Cinema implements ICinema{

	private static HashMap<Character, HashMap<Integer, SeatStatus>> seats;
	private static ArrayList<Lock> locks = new ArrayList<Lock>();
	
	@Override
	public void init(int rows, int columns) throws ICinemaInitCinemaException {
		seats.clear();
		if (rows <= 26 && rows >= 1 && columns <= 100 && columns >= 1) {
			for (char i = 'A'; i < 65 + rows; i++) {
				HashMap<Integer, SeatStatus> inner = new HashMap<Integer, SeatStatus>();
				for (int j = 1; j <= columns; j++) {
					inner.put(j, SeatStatus.FREE);
				}
				seats.put(i, inner);
			}
		}
		else {
			throw new ICinemaInitCinemaException("Seats are out of range", new CinemaException());
		}
	}

	@Override
	public ArrayOfSeat getAllSeats() throws ICinemaGetAllSeatsCinemaException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SeatStatus getSeatStatus(Seat seat) throws ICinemaGetSeatStatusCinemaException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lock(Seat seat, int count) throws ICinemaLockCinemaException {
		// TODO Auto-generated method stub
		return "test";
	}

	@Override
	public void unlock(String lockId) throws ICinemaUnlockCinemaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reserve(String lockId) throws ICinemaReserveCinemaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buy(String lockId) throws ICinemaBuyCinemaException {
		// TODO Auto-generated method stub
		
	}

}
