-- --------------------------------------------------------
-- Host:                         localhost
-- Server version:               9.5.0 - MySQL Community Server - GPL
-- Server OS:                    Linux
-- HeidiSQL Version:             11.2.0.6213
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for db_teste_serasa
CREATE DATABASE IF NOT EXISTS `db_teste_serasa` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `db_teste_serasa`;

CREATE TABLE IF NOT EXISTS `balanca` (
                                         `id` int NOT NULL AUTO_INCREMENT,
                                         `id_filial` int NOT NULL,
                                         `identificador_autorizacao` varchar(36) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `IDX_BALANCA_ID_FILIAL` (`id_filial`),
    CONSTRAINT `FK_BALANCA_ID_FILIAL` FOREIGN KEY (`id_filial`) REFERENCES `filial` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `caminhao` (
                                          `placa` varchar(7) NOT NULL,
    `tara` float NOT NULL DEFAULT 0,
    PRIMARY KEY (`placa`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `filial` (
                                        `id` int NOT NULL AUTO_INCREMENT,
                                        `nome` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `pesagens` (
                                          `id` int NOT NULL AUTO_INCREMENT,
                                          `custo_carga` float NOT NULL,
                                          `id_balanca` int NOT NULL,
                                          `id_tipo_grao` int NOT NULL,
                                          `peso_bruto_estabilizado` float NOT NULL,
                                          `peso_liquido` float NOT NULL,
                                          `tara` float NOT NULL,
                                          `data_hora_pesagem` datetime(6) DEFAULT NULL,
    `placa` varchar(7) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `IDX_PESAGENS_CAMINHAO` (`placa`),
    KEY `IDX_PESAGENS_TIPO_GRAO` (`id_tipo_grao`),
    KEY `IDX_PESAGENS_BALANCA` (`id_balanca`),
    CONSTRAINT `FK_PESAGENS_CAMINHAO` FOREIGN KEY (`placa`) REFERENCES `caminhao` (`placa`),
    CONSTRAINT `FK_PESAGENS_TIPO_GRAO` FOREIGN KEY (`id_tipo_grao`) REFERENCES `tipograo` (`id`),
    CONSTRAINT `FK_PESAGENS_BALANCA` FOREIGN KEY (`id_balanca`) REFERENCES `balanca` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `tipograo` (
                                          `id` int NOT NULL AUTO_INCREMENT,
                                          `custo_por_tonelada` float NOT NULL,
                                          `nome` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `transacaotransporte` (
                                                     `id` int NOT NULL AUTO_INCREMENT,
                                                     `id_grao` int NOT NULL,
                                                     `id_pesagem` int DEFAULT NULL,
                                                     `volume_comprado` float NOT NULL,
                                                     `data_hora_retorno` datetime(6) DEFAULT NULL,
    `data_hora_saida` datetime(6) DEFAULT NULL,
    `placa_caminhao` varchar(7) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `IDX_TRANSACAOTRANSPORTE_PLACA_CAMINHAO` (`placa_caminhao`),
    KEY `IDX_TRANSACAOTRANSPORTE_TIPO_GRAO` (`id_grao`),
    KEY `IDX_TRANSACAOTRANSPORTE_PESAGEM` (`id_pesagem`),
    CONSTRAINT `FK_TRANSACAOTRANSPORTE_PESAGEM` FOREIGN KEY (`id_pesagem`) REFERENCES `pesagens` (`id`),
    CONSTRAINT `FK_TRANSACAOTRANSPORTE_CAMINHAO` FOREIGN KEY (`placa_caminhao`) REFERENCES `caminhao` (`placa`),
    CONSTRAINT `FK_TRANSACAOTRANSPORTE_TIPO_GRAO` FOREIGN KEY (`id_grao`) REFERENCES `tipograo` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
