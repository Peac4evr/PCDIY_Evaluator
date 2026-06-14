package org.example.pcdiy_evaluator.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.pcdiy_evaluator.model.entity.HardwareComponent;

import java.util.List;

@Mapper
public interface HardwareComponentMapper {

    /**
     * 根据 ID 查询单个硬件（基础支撑）
     *
     * @param id 硬件主键
     * @return 硬件实体 DO
     */
    @Select("SELECT * FROM hardware_component WHERE id = #{id}")
    HardwareComponent selectById(@Param("id") Long id);

    /**
     * 根据 ID 列表批量查询硬件清单（核心性能优化点）
     * 将 5 次 DB 交互降为 1 次，一次性拉取装机单中的所有配件
     *
     * @param ids 硬件 ID 集合
     * @return 硬件实体列表
     */
    @Select({
            "<script>",
            "SELECT * FROM hardware_component WHERE id IN ",
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<HardwareComponent> selectBatchIds(@Param("ids") List<Long> ids);

    /**
     * 根据硬件类型查询全部硬件列表（供前端下拉选择使用）
     *
     * @param type 硬件类型 (CPU, MOTHERBOARD, MEMORY, GPU, PSU)
     * @return 该类型的所有硬件实体列表
     */
    @Select("SELECT * FROM hardware_component WHERE type = #{type}")
    List<HardwareComponent> selectByType(@Param("type") String type);
}