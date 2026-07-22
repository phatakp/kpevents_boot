package com.phatakp.kpevents.users.mappers;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.transactions.entity.Transaction;
import com.phatakp.kpevents.users.dto.request.UserCreateRequest;
import com.phatakp.kpevents.users.dto.response.*;
import com.phatakp.kpevents.users.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public static UserResponse toResponse(User user) {
        List<UserMembership> memberships = user.getMemberships().stream()
                .map(CommiteeMemberMapper::toMembership)
                .toList();

        return UserResponse.builder()
                .clerkId(user.getClerkId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .imageUrl(user.getImageUrl())
                .building(user.getBuilding())
                .flat(user.getFlat())
                .role(user.getRole())
                .memberships(memberships)
                .build();
    }
    public static ShortUser toShortUser(User user) {
        return ShortUser.builder()
                .clerkId(user.getClerkId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .building(user.getBuilding())
                .flat(user.getFlat())
                .build();
    }

    public static User toEntity(UserCreateRequest request) {
        return User.builder()
                .clerkId(request.clerkId())
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .imageUrl(request.imageUrl())
                .building(request.flatNumber().building())
                .flat(request.flatNumber().flat())
                .build();
    }

    public static UserBalance toUserBalance(User user, Committee committee) {
        if (user == null) return null;

        var filteredTxns = user.getUserTxns().stream()
                .filter(txn -> txn.getCommittee().equals(committee)).toList();

        //Get total balance
        Double totalBalance = filteredTxns.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        //Sum on the basis of year, txnType and donationType
        var balances = filteredTxns.stream()
                .collect(Collectors.toMap(txn->
                        List.of(txn.getCommittee(),
                                txn.getYear(),
                                txn.getTxnType(),
                                txn.getDonation()!=null?txn.getDonation().getType():"null"),
                        txn -> new BalanceStat(
                                txn.getCommittee(),
                                txn.getYear(),
                                txn.getTxnType(), txn.getDonation()!=null?txn.getDonation().getType().name():null,
                                txn.getAmount()),
                        (existing, replacement) -> new BalanceStat(
                                existing.committee(),
                                existing.year(),
                                existing.txnType(), existing.donationType(),
                                existing.balance()+replacement.balance())));

        return UserBalance.builder()
                .clerkId(user.getClerkId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .building(user.getBuilding())
                .flat(user.getFlat())
                .total(totalBalance)
                .balances(new ArrayList<>(balances.values()))
                .build();
    }

    public static UserBalance toUserBalance(User user) {
        if (user == null) return null;
        return UserBalance.builder()
                .clerkId(user.getClerkId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .building(user.getBuilding())
                .flat(user.getFlat())
                .total((double) 0)
                .balances(new ArrayList<>())
                .build();
    }
}
