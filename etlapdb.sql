-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Gép: 127.0.0.1
-- Létrehozás ideje: 2022. Dec 21. 10:20
-- Kiszolgáló verziója: 10.4.18-MariaDB
-- PHP verzió: 8.0.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Adatbázis: `etlapdb`
--

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `etlap`
--

CREATE TABLE `etlap` (
  `id` int(11) NOT NULL,
  `nev` varchar(100) COLLATE utf8mb4_hungarian_ci NOT NULL,
  `leiras` text COLLATE utf8mb4_hungarian_ci NOT NULL,
  `ar` int(11) NOT NULL,
  `kategoria_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_hungarian_ci;

--
-- A tábla adatainak kiíratása `etlap`
--

INSERT INTO `etlap` (`id`, `nev`, `leiras`, `ar`, `kategoria_id`) VALUES
(15, 'tojas', 'tojas', 11487, 2),
(16, 'Kakaos csiga', 'kakaos', 96, 9),
(17, 'Beigli', 'finom', 67, 9),
(18, 'Leves', 'hús leves', 50, 1),
(19, 'rántott csirkemell', '-', 150, 1);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `kategoria`
--

CREATE TABLE `kategoria` (
  `id` int(11) NOT NULL,
  `nev` varchar(50) COLLATE utf8mb4_hungarian_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_hungarian_ci;

--
-- A tábla adatainak kiíratása `kategoria`
--

INSERT INTO `kategoria` (`id`, `nev`) VALUES
(0, '-'),
(9, 'Desszert'),
(1, 'főétel'),
(2, 'reggeli'),
(6, 'Vacsora');

--
-- Indexek a kiírt táblákhoz
--

--
-- A tábla indexei `etlap`
--
ALTER TABLE `etlap`
  ADD PRIMARY KEY (`id`),
  ADD KEY `kategoria_id` (`kategoria_id`);

--
-- A tábla indexei `kategoria`
--
ALTER TABLE `kategoria`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nev` (`nev`);

--
-- A kiírt táblák AUTO_INCREMENT értéke
--

--
-- AUTO_INCREMENT a táblához `etlap`
--
ALTER TABLE `etlap`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT a táblához `kategoria`
--
ALTER TABLE `kategoria`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- Megkötések a kiírt táblákhoz
--

--
-- Megkötések a táblához `etlap`
--
ALTER TABLE `etlap`
  ADD CONSTRAINT `etlap_ibfk_1` FOREIGN KEY (`kategoria_id`) REFERENCES `kategoria` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
