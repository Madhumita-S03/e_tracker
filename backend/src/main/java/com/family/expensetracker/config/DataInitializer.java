package com.family.expensetracker.config;

import com.family.expensetracker.entity.Category;
import com.family.expensetracker.entity.User;
import com.family.expensetracker.repository.CategoryRepository;
import com.family.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedCategories();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }
        log.info("Seeding development family member accounts (CHANGE THESE PASSWORDS)...");

        String[][] members = {
                {"member1", "Family Member 1", "#4F46E5"},
                {"member2", "Family Member 2", "#059669"},
                {"member3", "Family Member 3", "#DB2777"},
                {"member4", "Family Member 4", "#D97706"}
        };

        for (String[] m : members) {
            User user = User.builder()
                    .username(m[0])
                    .password(passwordEncoder.encode("*****"))
                    .fullName(m[1])
                    .avatarColor(m[2])
                    .role(User.Role.MEMBER)
                    .enabled(true)
                    .build();
            userRepository.save(user);
        }
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) {
            return;
        }
        log.info("Seeding default expense categories...");

        String[][] categories = {
                {"Groceries", "Food and household supplies", "#22C55E", "shopping-cart"},
                {"Utilities", "Electricity, water, gas, internet", "#3B82F6", "bolt"},
                {"Rent/Mortgage", "Housing payments", "#8B5CF6", "home"},
                {"Transportation", "Fuel, public transit, maintenance", "#F59E0B", "car"},
                {"Healthcare", "Medical, dental, pharmacy", "#EF4444", "heart"},
                {"Education", "School fees, books, courses", "#0EA5E9", "book"},
                {"Entertainment", "Movies, outings, subscriptions", "#EC4899", "film"},
                {"Dining Out", "Restaurants and takeaway", "#F97316", "utensils"},
                {"Insurance", "Life, health, vehicle insurance", "#14B8A6", "shield"},
                {"Miscellaneous", "Everything else", "#6B7280", "tag"}
        };

        for (String[] c : categories) {
            Category category = Category.builder()
                    .name(c[0])
                    .description(c[1])
                    .colorCode(c[2])
                    .icon(c[3])
                    .defaultCategory(true)
                    .build();
            categoryRepository.save(category);
        }
    }
}
