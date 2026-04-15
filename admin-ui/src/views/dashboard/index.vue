<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">用户总数</div>
              <div class="stat-value">1,024</div>
            </div>
            <el-icon class="stat-icon" style="color: #409eff;"><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">角色数量</div>
              <div class="stat-value">12</div>
            </div>
            <el-icon class="stat-icon" style="color: #67c23a;"><Stamp /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">菜单数量</div>
              <div class="stat-value">36</div>
            </div>
            <el-icon class="stat-icon" style="color: #e6a23c;"><Menu /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">部门数量</div>
              <div class="stat-value">8</div>
            </div>
            <el-icon class="stat-icon" style="color: #f56c6c;"><OfficeBuilding /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>系统公告</span>
          </template>
          <el-timeline>
            <el-timeline-item timestamp="2024-01-15" placement="top">
              <el-card>
                <h4>系统升级通知</h4>
                <p>后台管理系统已升级至最新版本，新增多项功能优化。</p>
              </el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2024-01-10" placement="top">
              <el-card>
                <h4>新功能上线</h4>
                <p>新增数据权限管理功能，支持自定义数据范围。</p>
              </el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2024-01-05" placement="top">
              <el-card>
                <h4>安全更新</h4>
                <p>修复了若干安全漏洞，建议所有用户更新。</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-links">
            <el-button type="primary" @click="$router.push('/system/user')">
              <el-icon><User /></el-icon>
              用户管理
            </el-button>
            <el-button type="success" @click="$router.push('/system/role')">
              <el-icon><Stamp /></el-icon>
              角色管理
            </el-button>
            <el-button type="warning" @click="$router.push('/system/menu')">
              <el-icon><Menu /></el-icon>
              菜单管理
            </el-button>
            <el-button type="danger" @click="$router.push('/system/dept')">
              <el-icon><OfficeBuilding /></el-icon>
              部门管理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { User, Stamp, Menu, OfficeBuilding } from '@element-plus/icons-vue'
</script>

<style lang="scss" scoped>
.dashboard-container {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .stat-info {
        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-bottom: 8px;
        }
        .stat-value {
          font-size: 28px;
          font-weight: 600;
          color: #303133;
        }
      }

      .stat-icon {
        font-size: 48px;
        opacity: 0.8;
      }
    }
  }

  .quick-links {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .el-button {
      width: 100%;
      justify-content: flex-start;
    }
  }
}
</style>
