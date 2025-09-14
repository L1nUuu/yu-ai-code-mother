<template>
  <Footer class="footer">
    <div class="footer-content">
      <p class="copyright">
        编程导航原创项目 by 
        <a href="https://www.codefather.cn" target="_blank" rel="noopener noreferrer">
          程序员鱼皮
        </a>
      </p>
    </div>
  </Footer>
</template>

<script setup lang="ts">
import { Layout } from 'ant-design-vue'

const { Footer } = Layout
</script>

<style scoped>
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  border-top: 1px solid #f0f0f0;
  padding: 12px 0;
  text-align: center;
}

.footer-content {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 24px;
}

.copyright {
  margin: 0;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.65);
}

.copyright a {
  color: #1890ff;
  text-decoration: none;
}

.copyright a:hover {
  text-decoration: underline;
}
</style>