-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 14, 2026 at 05:23 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `laundryr`
--

-- --------------------------------------------------------

--
-- Table structure for table `api_tokens`
--

CREATE TABLE `api_tokens` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `token` char(64) NOT NULL,
  `revoked_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `api_tokens`
--

INSERT INTO `api_tokens` (`id`, `user_id`, `token`, `revoked_at`, `created_at`, `updated_at`) VALUES
(1, 1, '579d9d484ce96743bb7b0666c7fb35ce46b3fe3bce6eec51b6b2ff4fd6896b3d', '2026-04-14 12:05:37', '2026-04-14 12:04:16', '2026-04-14 12:05:37'),
(2, 3, '452d89c2762ed338b1c745f77efbc0baceb108b1aecbb49677fe5c1e12abca2c', '2026-04-14 12:05:54', '2026-04-14 12:05:46', '2026-04-14 12:05:54'),
(3, 2, '3f45524111e2e8066f78073ad8bbfc50ffbf35c4397bf99420b042264fb1db12', '2026-04-14 12:06:35', '2026-04-14 12:06:06', '2026-04-14 12:06:35'),
(4, 3, 'fca51e18a7e0c24f630619e23046513718cc4048a1221013cbf46d954b6aa148', '2026-04-14 13:06:19', '2026-04-14 12:06:52', '2026-04-14 13:06:19'),
(5, 1, 'b32f58b030d0ad30a77e4284f42b33390daef3f8a3da8740fc3bc7de04fcdf24', NULL, '2026-04-14 12:42:13', '2026-04-14 12:42:13'),
(6, 1, '59b61a9ae6a8c6287539060bbf0e343016580a6d21095474a7824137d6130c2c', '2026-04-14 13:26:47', '2026-04-14 13:06:32', '2026-04-14 13:26:47'),
(7, 2, '279f16fd1bf982f70ea1502939970763413b8ca3e47b2abc0763faeb187bf2c0', '2026-04-14 14:19:38', '2026-04-14 13:27:00', '2026-04-14 14:19:38'),
(8, 4, '8cdd3739244f34b25f0f97b707c77cc0d2d2abe5e2e11f95f3a53bc9b1adc36c', '2026-04-14 14:21:18', '2026-04-14 14:21:14', '2026-04-14 14:21:18'),
(9, 4, '744f0c4cb9ba78430d785e804cc6da087cd6a2c2713f4dea46b648d2bd2bbfe9', '2026-04-14 14:21:48', '2026-04-14 14:21:45', '2026-04-14 14:21:48');

-- --------------------------------------------------------

--
-- Table structure for table `complaints`
--

CREATE TABLE `complaints` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `order_id` bigint(20) UNSIGNED NOT NULL,
  `customer_id` bigint(20) UNSIGNED NOT NULL,
  `item_name` varchar(255) NOT NULL,
  `damage_type` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `photo_url` varchar(255) DEFAULT NULL,
  `status` enum('submitted','in_review','resolved','rejected') NOT NULL DEFAULT 'submitted',
  `reviewed_by` bigint(20) UNSIGNED DEFAULT NULL,
  `reviewed_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(25) NOT NULL,
  `address` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`id`, `user_id`, `name`, `phone`, `address`, `created_at`, `updated_at`) VALUES
(1, 3, 'Pelanggan Demo', '083333333333', 'Alamat pelanggan demo', '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(2, NULL, 'Customer Walk-In', '08123456789', 'Alamat customer', '2026-04-14 12:04:46', '2026-04-14 12:04:46'),
(3, NULL, 'mai bubub kecayangan', '08511021574', 'bebeb tercayang', '2026-04-14 13:33:40', '2026-04-14 13:33:40'),
(4, 4, 'bubub cayang', '085111021574', 'bebeb', '2026-04-14 14:21:14', '2026-04-14 14:21:14');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `invoice_number` varchar(50) NOT NULL,
  `customer_id` bigint(20) UNSIGNED NOT NULL,
  `created_by` bigint(20) UNSIGNED NOT NULL,
  `service_type_id` bigint(20) UNSIGNED NOT NULL,
  `laundry_weight_kg` decimal(8,2) NOT NULL DEFAULT 0.00,
  `price_per_kg_snapshot` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `subtotal_kg` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `subtotal_special` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `grand_total` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `payment_status` enum('belum_lunas','lunas') NOT NULL DEFAULT 'belum_lunas',
  `current_status` varchar(50) NOT NULL DEFAULT 'DITERIMA',
  `completed_at` timestamp NULL DEFAULT NULL,
  `picked_up_at` timestamp NULL DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id`, `invoice_number`, `customer_id`, `created_by`, `service_type_id`, `laundry_weight_kg`, `price_per_kg_snapshot`, `subtotal_kg`, `subtotal_special`, `grand_total`, `payment_status`, `current_status`, `completed_at`, `picked_up_at`, `notes`, `created_at`, `updated_at`) VALUES
(1, 'INV-20260414-0001', 2, 1, 1, 3.00, 7000, 21000, 18000, 39000, 'lunas', 'DIAMBIL', '2026-04-14 12:05:25', '2026-04-14 12:05:26', NULL, '2026-04-14 12:04:46', '2026-04-14 12:05:31'),
(2, 'INV-20260414-0002', 3, 2, 2, 10.00, 6000, 60000, 18000, 78000, 'belum_lunas', 'DITERIMA', NULL, NULL, NULL, '2026-04-14 13:33:40', '2026-04-14 13:33:40');

-- --------------------------------------------------------

--
-- Table structure for table `order_special_items`
--

CREATE TABLE `order_special_items` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `order_id` bigint(20) UNSIGNED NOT NULL,
  `special_item_category_id` bigint(20) UNSIGNED NOT NULL,
  `category_name_snapshot` varchar(255) NOT NULL,
  `unit_snapshot` varchar(40) NOT NULL DEFAULT 'item',
  `price_per_item_snapshot` int(10) UNSIGNED NOT NULL,
  `quantity` int(10) UNSIGNED NOT NULL,
  `subtotal` int(10) UNSIGNED NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_special_items`
--

INSERT INTO `order_special_items` (`id`, `order_id`, `special_item_category_id`, `category_name_snapshot`, `unit_snapshot`, `price_per_item_snapshot`, `quantity`, `subtotal`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 'Selimut', 'buah', 18000, 1, 18000, '2026-04-14 12:04:46', '2026-04-14 12:04:46'),
(2, 2, 1, 'Selimut', 'buah', 18000, 1, 18000, '2026-04-14 13:33:40', '2026-04-14 13:33:40');

-- --------------------------------------------------------

--
-- Table structure for table `order_status_logs`
--

CREATE TABLE `order_status_logs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `order_id` bigint(20) UNSIGNED NOT NULL,
  `previous_status` varchar(50) DEFAULT NULL,
  `new_status` varchar(50) NOT NULL,
  `changed_by` bigint(20) UNSIGNED NOT NULL,
  `changed_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_status_logs`
--

INSERT INTO `order_status_logs` (`id`, `order_id`, `previous_status`, `new_status`, `changed_by`, `changed_at`, `created_at`, `updated_at`) VALUES
(1, 1, NULL, 'DITERIMA', 1, '2026-04-14 12:04:46', '2026-04-14 12:04:46', '2026-04-14 12:04:46'),
(2, 1, 'DITERIMA', 'DICUCI', 1, '2026-04-14 12:05:20', '2026-04-14 12:05:20', '2026-04-14 12:05:20'),
(3, 1, 'DICUCI', 'DIJEMUR', 1, '2026-04-14 12:05:22', '2026-04-14 12:05:22', '2026-04-14 12:05:22'),
(4, 1, 'DIJEMUR', 'DISETRIKA', 1, '2026-04-14 12:05:23', '2026-04-14 12:05:23', '2026-04-14 12:05:23'),
(5, 1, 'DISETRIKA', 'SELESAI', 1, '2026-04-14 12:05:25', '2026-04-14 12:05:25', '2026-04-14 12:05:25'),
(6, 1, 'SELESAI', 'DIAMBIL', 1, '2026-04-14 12:05:26', '2026-04-14 12:05:26', '2026-04-14 12:05:26'),
(7, 2, NULL, 'DITERIMA', 2, '2026-04-14 13:33:40', '2026-04-14 13:33:40', '2026-04-14 13:33:40');

-- --------------------------------------------------------

--
-- Table structure for table `payment_logs`
--

CREATE TABLE `payment_logs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `order_id` bigint(20) UNSIGNED NOT NULL,
  `previous_status` enum('belum_lunas','lunas') NOT NULL,
  `new_status` enum('belum_lunas','lunas') NOT NULL,
  `amount_paid` int(10) UNSIGNED NOT NULL,
  `verified_by` bigint(20) UNSIGNED NOT NULL,
  `verified_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `notes` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payment_logs`
--

INSERT INTO `payment_logs` (`id`, `order_id`, `previous_status`, `new_status`, `amount_paid`, `verified_by`, `verified_at`, `notes`, `created_at`, `updated_at`) VALUES
(1, 1, 'belum_lunas', 'lunas', 39000, 1, '2026-04-14 12:05:31', NULL, '2026-04-14 12:05:31', '2026-04-14 12:05:31');

-- --------------------------------------------------------

--
-- Table structure for table `service_prices`
--

CREATE TABLE `service_prices` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `service_type_id` bigint(20) UNSIGNED NOT NULL,
  `price_per_kg` int(10) UNSIGNED NOT NULL,
  `effective_from` date NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `service_prices`
--

INSERT INTO `service_prices` (`id`, `service_type_id`, `price_per_kg`, `effective_from`, `created_at`, `updated_at`) VALUES
(1, 1, 7000, '2026-04-14', '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(2, 2, 6000, '2026-04-14', '2026-04-14 12:04:10', '2026-04-14 12:04:10');

-- --------------------------------------------------------

--
-- Table structure for table `service_types`
--

CREATE TABLE `service_types` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `code` varchar(100) NOT NULL,
  `name` varchar(255) NOT NULL,
  `workflow` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`workflow`)),
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `service_types`
--

INSERT INTO `service_types` (`id`, `code`, `name`, `workflow`, `is_active`, `created_at`, `updated_at`) VALUES
(1, 'FULL_LAUNDRY', 'Full Laundry', '[\"DITERIMA\", \"DICUCI\", \"DIJEMUR\", \"DISETRIKA\", \"SELESAI\", \"DIAMBIL\"]', 1, '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(2, 'SETRIKA_SAJA', 'Setrika Saja', '[\"DITERIMA\", \"DISETRIKA\", \"SELESAI\", \"DIAMBIL\"]', 1, '2026-04-14 12:04:10', '2026-04-14 12:04:10');

-- --------------------------------------------------------

--
-- Table structure for table `special_item_categories`
--

CREATE TABLE `special_item_categories` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `unit` varchar(40) NOT NULL DEFAULT 'item',
  `price_per_item` int(10) UNSIGNED NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `special_item_categories`
--

INSERT INTO `special_item_categories` (`id`, `name`, `unit`, `price_per_item`, `is_active`, `created_at`, `updated_at`) VALUES
(1, 'Selimut', 'buah', 18000, 1, '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(2, 'Bed cover', 'buah', 25000, 1, '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(3, 'Sprei', 'lembar', 15000, 1, '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(4, 'Gorden', 'lembar', 20000, 1, '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(5, 'Sepatu', 'pasang', 30000, 1, '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(6, 'Tas', 'buah', 22000, 1, '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(7, 'Boneka', 'buah', 16000, 1, '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(8, 'Jaket Tebal', 'buah', 17000, 1, '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(9, 'Karpet Kecil', 'buah', 35000, 1, '2026-04-14 12:04:10', '2026-04-14 12:04:10');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone` varchar(25) DEFAULT NULL,
  `role` enum('admin','staff','customer') NOT NULL DEFAULT 'customer',
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `phone`, `role`, `password`, `created_at`, `updated_at`) VALUES
(1, 'Admin Laundryr', 'admin@gmail.com', '081111111111', 'admin', '$2y$10$cxCXu/cpvvtLHLRaXgF4kexXvEFl06RraGe6oyJ.qM.JZmV0JxtZa', '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(2, 'Petugas Laundryr', 'petugas@gmail.com', '082222222222', 'staff', '$2y$10$cxCXu/cpvvtLHLRaXgF4kexXvEFl06RraGe6oyJ.qM.JZmV0JxtZa', '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(3, 'Pelanggan Demo', 'pelanggan@gmail.com', '083333333333', 'customer', '$2y$10$cxCXu/cpvvtLHLRaXgF4kexXvEFl06RraGe6oyJ.qM.JZmV0JxtZa', '2026-04-14 12:04:10', '2026-04-14 12:04:10'),
(4, 'bubub cayang', 'akunsecool3@gmail.com', '085111021574', 'customer', '$2y$10$0v5lSVysNKPtlv76w529zONYBE3h5M1xGXCtSplDAuAAxHK6zNNZW', '2026-04-14 14:21:14', '2026-04-14 14:21:14');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `api_tokens`
--
ALTER TABLE `api_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `fk_api_tokens_user` (`user_id`);

--
-- Indexes for table `complaints`
--
ALTER TABLE `complaints`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_complaints_order` (`order_id`),
  ADD KEY `fk_complaints_customer` (`customer_id`),
  ADD KEY `fk_complaints_reviewer` (`reviewed_by`);

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_customers_user` (`user_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `invoice_number` (`invoice_number`),
  ADD KEY `fk_orders_customer` (`customer_id`),
  ADD KEY `fk_orders_creator` (`created_by`),
  ADD KEY `fk_orders_service_type` (`service_type_id`);

--
-- Indexes for table `order_special_items`
--
ALTER TABLE `order_special_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_order_special_items_order` (`order_id`),
  ADD KEY `fk_order_special_items_category` (`special_item_category_id`);

--
-- Indexes for table `order_status_logs`
--
ALTER TABLE `order_status_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_order_status_logs_order` (`order_id`),
  ADD KEY `fk_order_status_logs_user` (`changed_by`);

--
-- Indexes for table `payment_logs`
--
ALTER TABLE `payment_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_payment_logs_order` (`order_id`),
  ADD KEY `fk_payment_logs_user` (`verified_by`);

--
-- Indexes for table `service_prices`
--
ALTER TABLE `service_prices`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uniq_service_price` (`service_type_id`,`effective_from`);

--
-- Indexes for table `service_types`
--
ALTER TABLE `service_types`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`);

--
-- Indexes for table `special_item_categories`
--
ALTER TABLE `special_item_categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `api_tokens`
--
ALTER TABLE `api_tokens`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `complaints`
--
ALTER TABLE `complaints`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `customers`
--
ALTER TABLE `customers`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `order_special_items`
--
ALTER TABLE `order_special_items`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `order_status_logs`
--
ALTER TABLE `order_status_logs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `payment_logs`
--
ALTER TABLE `payment_logs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `service_prices`
--
ALTER TABLE `service_prices`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `service_types`
--
ALTER TABLE `service_types`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `special_item_categories`
--
ALTER TABLE `special_item_categories`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `api_tokens`
--
ALTER TABLE `api_tokens`
  ADD CONSTRAINT `fk_api_tokens_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `complaints`
--
ALTER TABLE `complaints`
  ADD CONSTRAINT `fk_complaints_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_complaints_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_complaints_reviewer` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `customers`
--
ALTER TABLE `customers`
  ADD CONSTRAINT `fk_customers_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `fk_orders_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_orders_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_orders_service_type` FOREIGN KEY (`service_type_id`) REFERENCES `service_types` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `order_special_items`
--
ALTER TABLE `order_special_items`
  ADD CONSTRAINT `fk_order_special_items_category` FOREIGN KEY (`special_item_category_id`) REFERENCES `special_item_categories` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_order_special_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `order_status_logs`
--
ALTER TABLE `order_status_logs`
  ADD CONSTRAINT `fk_order_status_logs_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_order_status_logs_user` FOREIGN KEY (`changed_by`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `payment_logs`
--
ALTER TABLE `payment_logs`
  ADD CONSTRAINT `fk_payment_logs_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_payment_logs_user` FOREIGN KEY (`verified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `service_prices`
--
ALTER TABLE `service_prices`
  ADD CONSTRAINT `fk_service_prices_service_type` FOREIGN KEY (`service_type_id`) REFERENCES `service_types` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
