package com.hyperledger.export.models;

/**
 * Интерфейс для именованных сущностей
 */
public interface HLNamed {
	/**
	 * Получить полное имя
	 * @return полное имя
	 */
	public String getFullName();
	/**
	 * Получить имя
	 * @return имя
	 */
	public String getName();
}
