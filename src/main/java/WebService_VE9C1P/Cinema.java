package WebService_VE9C1P;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

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
import seatreservation.Lock;

@WebService(name = "CinemaService", 
			portName = "ICinema_HttpSoap11_Port", 
			targetNamespace = "http://www.iit.bme.hu/soi/hw/SeatReservation", 
			endpointInterface = "seatreservation.ICinema", 
			wsdlLocation = "WEB-INF/wsdl/SeatReservation.wsdl")
public class Cinema implements ICinema{

	private static HashMap<String, HashMap<String, SeatStatus>> seats = new HashMap<String, HashMap<String, SeatStatus>>();
	private static HashMap<String, Lock> locks = new HashMap<String, Lock>();
	
	private boolean validSeat(Seat seat) {
		if(!seats.containsKey(seat.getRow()) || !seats.get(seat.getRow()).containsKey(seat.getColumn()))
			return false;
		return true;
	}
	
	
	@Override
	public void init(int rows, int columns) throws ICinemaInitCinemaException {
		seats.clear();
		locks.clear();
		if (rows <= 26 && rows >= 1 && columns <= 100 && columns >= 1) {
			for (char i = 'A'; i < 65 + rows; i++) {
				HashMap<String, SeatStatus> inner = new HashMap<String, SeatStatus>();
				for (int j = 1; j <= columns; j++) {
					inner.put(Integer.toString(j), SeatStatus.FREE);
				}
				seats.put(Character.toString(i), inner);
			}
		}
		else {
			throw new ICinemaInitCinemaException("Seats are out of range", new CinemaException());
		}
	}

	@Override
	public ArrayOfSeat getAllSeats() throws ICinemaGetAllSeatsCinemaException {
		ArrayOfSeat allSeats = new ArrayOfSeat();
		for (Entry<String, HashMap<String, SeatStatus>> entry : seats.entrySet()) {
			for (Entry<String, SeatStatus> innerEntry : entry.getValue().entrySet()) {
				Seat seat = new Seat();
				seat.setRow(entry.getKey());
				seat.setColumn(innerEntry.getKey());
				allSeats.getSeat().add(seat);
			}
		}
		return allSeats;
	}

	@Override
	public SeatStatus getSeatStatus(Seat seat) throws ICinemaGetSeatStatusCinemaException {
		if(!validSeat(seat)) 
			throw new ICinemaGetSeatStatusCinemaException("Bad seat number", new CinemaException());
		return seats.get(seat.getRow()).get(seat.getColumn());
	}

	@Override
	public String lock(Seat seat, int count) throws ICinemaLockCinemaException {
		Seat endSeat = new Seat();
		endSeat.setRow(seat.getRow());
		endSeat.setColumn(Integer.toString((Integer.parseInt(seat.getColumn())+count-1)));
		if(!validSeat(seat) || !validSeat(endSeat))
			throw new ICinemaLockCinemaException("Not enough seats", new CinemaException());
		
		String uniqueID = UUID.randomUUID().toString();
		Lock lock = new Lock();
		lock.setCount(count);
		lock.setSeat(seat);
		for (int i = 0; i < count; i++) {
			if (seats.get(seat.getRow()).get(Integer.toString((Integer.parseInt(seat.getColumn())+i))) != SeatStatus.FREE)
				throw new ICinemaLockCinemaException("Seat is not free", new CinemaException());
		}
		for (int i = 0; i < count; i++) {
			seats.get(seat.getRow()).replace(Integer.toString((Integer.parseInt(seat.getColumn())+i)), SeatStatus.LOCKED);
		}
		locks.put(uniqueID, lock);
		return uniqueID;
	}

	@Override
	public void unlock(String lockId) throws ICinemaUnlockCinemaException {
		if (!locks.containsKey(lockId)) {
			throw new ICinemaUnlockCinemaException("Lock does not exist", new CinemaException());
		}
		Lock lock = locks.get(lockId);
		Seat seat = lock.getSeat();
		for (int i = 0; i < lock.getCount(); i++) {
			seats.get(seat.getRow()).replace(Integer.toString((Integer.parseInt(seat.getColumn())+i)), SeatStatus.FREE);
		}
		locks.remove(lockId);
	}

	@Override
	public void reserve(String lockId) throws ICinemaReserveCinemaException {
		if (!locks.containsKey(lockId)) {
			throw new ICinemaReserveCinemaException("Lock does not exist", new CinemaException());
		}
		Lock lock = locks.get(lockId);
		Seat seat = lock.getSeat();
		for (int i = 0; i < lock.getCount(); i++) {
			seats.get(seat.getRow()).replace(Integer.toString((Integer.parseInt(seat.getColumn())+i)), SeatStatus.RESERVED);
		}
		locks.remove(lockId);
	}

	@Override
	public void buy(String lockId) throws ICinemaBuyCinemaException {
		if (!locks.containsKey(lockId)) {
			throw new ICinemaBuyCinemaException("Lock does not exist", new CinemaException());
		}
		Lock lock = locks.get(lockId);
		Seat seat = lock.getSeat();
		for (int i = 0; i < lock.getCount(); i++) {
			seats.get(seat.getRow()).replace(Integer.toString((Integer.parseInt(seat.getColumn())+i)), SeatStatus.SOLD);
		}
		locks.remove(lockId);
	}

}
